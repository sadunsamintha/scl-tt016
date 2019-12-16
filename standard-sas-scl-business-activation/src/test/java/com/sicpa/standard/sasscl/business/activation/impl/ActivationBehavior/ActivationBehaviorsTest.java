package com.sicpa.standard.sasscl.business.activation.impl.ActivationBehavior;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionMode;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.Activation;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.MaintenanceActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.RefeedActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize.DefaultProductFinalizerBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode.DefaultActivationInvalidCodeHandler;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.CodeTypeValidator;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.NoProductValidator;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.devices.camera.CameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionSKUProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;

import junit.framework.Assert;

public class ActivationBehaviorsTest {

	private ProductStatus status;

	private ExportActivationBehavior exportActiBehav;
	private MaintenanceActivationBehavior maintActiBehav;
	private IAuthenticator authenticator;
	private ProductionParameters productionParameters;
	private Code code;
	private BlobDetectionUtils blobDetectionUtils;
	private ActivationBehaviorProvider activationBehaviorProvider = new ActivationBehaviorProvider();
	
	ICameraAdaptor cameraAdaptor = new CameraAdaptor();

	private static CodeType codeType;

	@Before
	public void setUp() {

		code = new Code("1");
		CodeType codeType = new CodeType(1);
		productionParameters = new ProductionParameters();
		SKU sku = new SKU(1);
		sku.setCodeType(codeType);
		productionParameters.setSku(sku);

		exportActiBehav = new ExportActivationBehavior();
		exportActiBehav.setProductionParameters(productionParameters);

		maintActiBehav = new MaintenanceActivationBehavior();
		maintActiBehav.setProductionParameters(productionParameters);

		BlobDetectionProvider blobDetectionProvider =  new BlobDetectionSKUProvider(productionParameters, BlobDetectionMode.NONE, false);
		blobDetectionUtils = new BlobDetectionUtils(blobDetectionProvider);
		exportActiBehav.setBlobDetectionUtils(blobDetectionUtils);

	}

	@Test
	public void testExport() {

		Activation activation = new Activation();
		activation.setActivationBehaviorProvider(activationBehaviorProvider);
		activation.setProductionBatchProvider(new ProductionBatchProvider());
		activationBehaviorProvider.set(exportActiBehav);
		EventBusService.register(activation);

		Object statusCatcher = new Object() {
			@Subscribe
			public void catchEvt(NewProductEvent evt) {
				status = evt.getProduct().getStatus();
			}
		};
		EventBusService.register(statusCatcher);

		final boolean[] msgCatched = { false };
		Object msgCatcher = new Object() {
			@Subscribe
			public void catchEvt(MessageEvent evt) {
				msgCatched[0] = evt.getKey().equals(MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT);
			}
		};

		EventBusService.register(msgCatcher);

		EventBusService.post(new CameraBadCodeEvent(new Code("")));
		Assert.assertEquals(ProductStatus.EXPORT, status);

		activation.receiveCameraCode(new CameraGoodCodeEvent(new Code("132465")));
	}

	@Test
	public void testMaintenance() {

		Product product = maintActiBehav.receiveCode(code, true);
		Assert.assertEquals(ProductStatus.MAINTENANCE, product.getStatus());

		// status remains same, ignoring good/bad code in maintenance mode
		product = maintActiBehav.receiveCode(code, false);
		Assert.assertEquals(ProductStatus.MAINTENANCE, product.getStatus());
	}

	@Test
	public void testStandard() {

		// setup code type to be validated in the activation component (for code
		// type checking)
		codeType = new CodeType(1);
		SKU sku = new SKU();
		sku.setCodeType(codeType);
		productionParameters.setSku(sku);

		StandardActivationBehavior standardBehav = new StandardActivationBehavior();
		AuthenticatorModeProvider authenticatorModeProvider = new AuthenticatorModeProvider();
		standardBehav.setAuthenticatorModeProvider(authenticatorModeProvider);
		ProductionConfigProvider productionConfigProvider = new ProductionConfigProvider();
		productionConfigProvider.set(Mockito.mock(IProductionConfig.class));
		standardBehav.setProductionConfigProvider(productionConfigProvider);
		AuthenticatorProvider provider = new AuthenticatorProvider();
		provider.set(new DummyAuthenticator());
		standardBehav.setAuthenticatorProvider(provider);
		standardBehav.setProductFinalizer(new DefaultProductFinalizerBehavior());
		standardBehav.setProductionParameters(productionParameters);

		CodeTypeValidator action = new CodeTypeValidator();
		action.setProductionParameters(productionParameters);
		standardBehav.setProductValidator(action);
		standardBehav.setInvalidCodeHandler(new DefaultActivationInvalidCodeHandler());

		Product product = standardBehav.receiveCode(code, true);
		Assert.assertEquals(ProductStatus.AUTHENTICATED, product.getStatus());

		product = standardBehav.receiveCode(code, false);
		Assert.assertEquals(ProductStatus.UNREAD, product.getStatus());

		// set different code type
		SKU sku2 = new SKU();
		sku2.setCodeType(new CodeType(2));
		productionParameters.setSku(sku2);

		product = standardBehav.receiveCode(code, true);
		Assert.assertEquals(ProductStatus.TYPE_MISMATCH, product.getStatus());

		// set standardBehav with NoCodeTypeChecker
		standardBehav.setProductValidator(new NoProductValidator());
		product = standardBehav.receiveCode(code, true);
		// status is authenticated instead of type mismatch
		Assert.assertEquals(ProductStatus.AUTHENTICATED, product.getStatus());
	}

	@Test
	public void testRefeedActivationBehavior() {
		codeType = new CodeType(1);
		SKU sku = new SKU();
		sku.setCodeType(codeType);
		productionParameters.setSku(sku);

		RefeedActivationBehavior behavior = new RefeedActivationBehavior();
		ProductionConfigProvider productionConfigProvider = new ProductionConfigProvider();
		productionConfigProvider.set(Mockito.mock(IProductionConfig.class));
		behavior.setProductionConfigProvider(productionConfigProvider);
		AuthenticatorModeProvider authenticatorModeProvider = new AuthenticatorModeProvider();
		behavior.setAuthenticatorModeProvider(authenticatorModeProvider);
		AuthenticatorProvider provider = new AuthenticatorProvider();
		provider.set(new DummyAuthenticator());
		behavior.setAuthenticatorProvider(provider);
		behavior.setProductFinalizer(new DefaultProductFinalizerBehavior());

		CodeTypeValidator action = new CodeTypeValidator();
		action.setProductionParameters(productionParameters);
		behavior.setProductValidator(action);
		behavior.setInvalidCodeHandler(new DefaultActivationInvalidCodeHandler());
		behavior.setProductionParameters(productionParameters);

		Product product = behavior.receiveCode(code, true);
		Assert.assertEquals(ProductStatus.REFEED, product.getStatus());

		product = behavior.receiveCode(code, false);
		Assert.assertEquals(ProductStatus.UNREAD, product.getStatus());
	}

	static class DummyAuthenticator implements IAuthenticator {

		private static final long serialVersionUID = 1L;

		@Override
		public IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException {
			DecodedCameraCode result = new DecodedCameraCode();
			result.setAuthenticated(true);
			result.setBatchId(100);
			result.setCodeType(ActivationBehaviorsTest.codeType);
			result.setSequence(1);
			return result;
		}

		@Override
		public IDecodedResult decode(String mode, String encryptedCode, CodeType codeType) throws CryptographyException {
			DecodedCameraCode result = new DecodedCameraCode();
			result.setAuthenticated(true);
			result.setBatchId(100);
			result.setCodeType(ActivationBehaviorsTest.codeType);
			result.setSequence(1);
			return result;
		}

	}
}
