package com.sicpa.standard.sasscl.business.activation.impl.ActivationBehavior.standard;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize.DefaultProductFinalizerBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize.IProductFinalizerBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode.IActivationInvalidCodeHandler;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.AbstractProductValidator;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.NoProductValidator;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class StandardActivationBehaviorTest {

	private static final int SEQUENCE = 33;
	private static final int BATCH_ID = 11;
	private static final CodeType CODE_TYPE = new CodeType(0);
	private static final Code CODE = new Code();
	private StandardActivationBehavior activationBehavior;
	private AuthenticatorProvider authenticatorProvider;
	private ProductionParameters productionParameters;
	private IActivationInvalidCodeHandler invalidCodeHandler;
	private IAuthenticator authenticator;
	private DecodedCameraCode decodedCameraCode;
	protected AbstractProductValidator standardActionBehavior;

	@Before
	public void setUp() throws Exception {
		TestHelper.initExecutor();

		standardActionBehavior = Mockito.mock(AbstractProductValidator.class);
		productionParameters = new ProductionParameters();
		invalidCodeHandler = Mockito.mock(IActivationInvalidCodeHandler.class);
		authenticator = Mockito.mock(IAuthenticator.class);
		decodedCameraCode = new DecodedCameraCode(CODE_TYPE, BATCH_ID, SEQUENCE, true);
		authenticatorProvider = new AuthenticatorProvider();
		authenticatorProvider.set(authenticator);

		activationBehavior = new StandardActivationBehavior();
		activationBehavior.setProductFinalizer(Mockito.mock(IProductFinalizerBehavior.class));
		activationBehavior.setProductValidator(new NoProductValidator());
		activationBehavior.setProductionParameters(productionParameters);
		activationBehavior.setAuthenticatorProvider(authenticatorProvider);
		activationBehavior.setInvalidCodeHandler(invalidCodeHandler);
		activationBehavior.setProductValidator(standardActionBehavior);

		IProductionConfig productionConfig = Mockito.mock(IProductionConfig.class);
		ProductionConfigProvider productionConfigProvder = new ProductionConfigProvider();
		productionConfigProvder.set(productionConfig);
		activationBehavior.setProductionConfigProvider(productionConfigProvder);
		
		AuthenticatorModeProvider authenticatorModeProvider=new AuthenticatorModeProvider();
		activationBehavior.setAuthenticatorModeProvider(authenticatorModeProvider);

	}

	@Test
	public void testReceiveCodeExceptionNoAuthenticator() {

		final boolean[] catched = { false };

		Object evtCatcher = new Object() {
			@Subscribe
			public void catchEvent(MessageEvent evt) {
				catched[0] = true;
			}
		};
		EventBusService.register(evtCatcher);

		authenticatorProvider.set(null);
		activationBehavior.receiveCode(CODE, true);
		TestHelper.runAllTasks();
		Assert.assertTrue(catched[0]);
	}

	@Test
	public void testReceiveCodeInvalidDefaultModeNoSKU() {

		Product product = activationBehavior.receiveCode(CODE, false);

		assertProduct(product, ProductionMode.STANDARD, null, ProductStatus.UNREAD);

		Mockito.verify(invalidCodeHandler).handleInvalidCode(product);
	}

	@Test
	public void testReceiveCodeInvalidNotDefaultModeSKU() {

		SKU sku = fixtureNoDefaultModeSKU();

		Product product = activationBehavior.receiveCode(CODE, false);

		assertProduct(product, ProductionMode.MAINTENANCE, sku, ProductStatus.UNREAD);

		Mockito.verify(invalidCodeHandler).handleInvalidCode(product);
	}

	@Test
	public void testReceiveCodeValidAuthenticated() throws Exception {
		activationBehavior.setProductFinalizer(new DefaultProductFinalizerBehavior());

		SKU sku = fixtureNoDefaultModeSKU();

		verifyAuthenticator();

		Product product = activationBehavior.receiveCode(CODE, true);

		assertProduct(product, ProductionMode.MAINTENANCE, sku, ProductStatus.UNREAD);

		assertCode();

		Mockito.verify(standardActionBehavior).validate(product, decodedCameraCode);
	}

	@Test
	public void testReceiveCodeValidNotAuthenticated() throws Exception {
		decodedCameraCode.setAuthenticated(false);

		SKU sku = fixtureNoDefaultModeSKU();

		verifyAuthenticator();

		Product product = activationBehavior.receiveCode(CODE, true);

		assertProduct(product, ProductionMode.MAINTENANCE, sku, ProductStatus.NOT_AUTHENTICATED);

		assertCode();

	}

	@Test
	public void testReceiveCodeValidException() throws Exception {

		SKU sku = fixtureNoDefaultModeSKU();

		verifyAuthenticator();

		Mockito.doThrow(new RuntimeException()).when(standardActionBehavior)
				.validate((Product) Mockito.anyObject(), Mockito.eq(decodedCameraCode));

		Product product = activationBehavior.receiveCode(CODE, true);

		assertProduct(product, ProductionMode.MAINTENANCE, sku, ProductStatus.NOT_AUTHENTICATED);

		assertCode();

	}

	private void assertProduct(final Product product, final ProductionMode productionMode, final SKU sku,
			final ProductStatus status) {
		assertEquals(sku, product.getSku());
		assertEquals(CODE, product.getCode());
		assertEquals(status, product.getStatus());
	}

	private void assertCode() {
		assertEquals(CODE_TYPE, CODE.getCodeType());
		assertEquals(BATCH_ID, CODE.getEncoderId());
		assertEquals(SEQUENCE, CODE.getSequence());
	}

	private SKU fixtureNoDefaultModeSKU() {
		SKU sku = new SKU();
		productionParameters.setSku(sku);
		productionParameters.setProductionMode(ProductionMode.MAINTENANCE);
		return sku;
	}

	private void verifyAuthenticator() throws CryptographyException {
		Mockito.when(authenticator.decode(null, CODE.getStringCode())).thenReturn(decodedCameraCode);
	}

}
