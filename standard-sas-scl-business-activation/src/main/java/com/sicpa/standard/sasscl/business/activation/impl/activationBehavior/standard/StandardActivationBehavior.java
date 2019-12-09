package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_NO_AUTHENTICATOR;
import static com.sicpa.standard.sasscl.model.ProductStatus.NOT_AUTHENTICATED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.provider.IProviderGetter;
import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize.IProductFinalizerBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode.IActivationInvalidCodeHandler;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.IProductValidator;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

/**
 * Activation behavior used for the standard production mode</br>
 * 
 * process for a valid code:<br>
 * <li>create a new product<br>
 * 
 * <li>call the authenticator to get the crypto result
 * 
 * <li>call the validityChecker, it is responsible to set the status of the product
 * 
 * <li>call the productFinalizer, it is responsible to check the status of the product and do what is needed like stop
 * the production if the status is <code>ProductStatus.NOT_AUTHENTICATED</code>
 * 
 * <br>
 * <br>
 * <br>
 * Process for an invalid code:<br>
 * 
 * <li>create a new product
 * 
 * <li>call invalidCodeHandler
 * 
 * 
 */
public class StandardActivationBehavior extends AbstractActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(StandardActivationBehavior.class);

	// to authenticate the code
	protected IProviderGetter<IAuthenticator> authenticatorProvider;

	// how to validate a product
	private IProductValidator productValidator;

	// what to do with an invalid code
	private IActivationInvalidCodeHandler invalidCodeHandler;

	// last step of the activation process for a valid code
	private IProductFinalizerBehavior productFinalizer;

	protected AuthenticatorModeProvider authenticatorModeProvider;

	private ProductionConfigProvider productionConfigProvider;

	@Override
	public Product receiveCode(Code code, boolean valid) {

		logger.debug("Code received = {} , Is good code = {}", code, valid);

		if (authenticatorProvider == null || authenticatorProvider.get() == null) {
			EventBusService.post(new MessageEvent(EXCEPTION_NO_AUTHENTICATOR));
			return null;
		}

		Product product = createNewProduct();
		populateProductWithSelectedProductionParameters(product);
		product.setCode(code);

		if (valid) {
			handleGoodCode(product);
			return product;

		} else {
			invalidCodeHandler.handleInvalidCode(product);
			return product;
		}
	}

	private void handleGoodCode(Product product) {
		DecodedCameraCode result = getDecodedCameraCode(product.getCode());
		if (result == null) {
			product.setStatus(NOT_AUTHENTICATED);
		} else {
			setProductInfoWithCryptoResult(product, result);
		}
		checkProduct(result, product);
		productFinalizer.finalize(product);
	}

	private void populateProductWithSelectedProductionParameters(Product p) {
		p.setSku(productionParameters.getSku());
		p.setPrinted(isPrinted());
	}

	private boolean isPrinted() {
		IProductionConfig pc = productionConfigProvider.get();
		if (pc.getPrinterConfigs() == null || pc.getPrinterConfigs().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private void setProductInfoWithCryptoResult(Product product, DecodedCameraCode code) {
		product.getCode().setCodeType(code.getCodeType());
		product.getCode().setEncoderId(code.getBatchId());
		product.getCode().setSequence(code.getSequence());
		product.getCode().setMode(code.getMode());
		product.getCode().setVersion(code.getVersion());
	}

	protected DecodedCameraCode getDecodedCameraCode(Code code) {
		try {
			return (DecodedCameraCode) authenticatorProvider.get().decode(authenticatorModeProvider.get(),
					code.getStringCode());
		} catch (CryptographyException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * Call the <code>validator</code> to check product if it is valid or not<br>
	 * Set the product status to <code>ProductStatus.NOT_AUTHENTICATED</code> if the result is not authenticated or if
	 * an exception occurs
	 */
	protected void checkProduct(DecodedCameraCode result, Product product) {
		try {
			if (result != null && result.isAuthenticated()) {
				productValidator.validate(product, result);
			} else {
				product.setStatus(NOT_AUTHENTICATED);
			}
		} catch (Exception e) {
			product.setStatus(NOT_AUTHENTICATED);
			logger.error("result: " + result, e);
		}
	}

	protected Product createNewProduct() {
		return new Product();
	}

	public void setAuthenticatorProvider(IProviderGetter<IAuthenticator> authenticatorProvider) {
		this.authenticatorProvider = authenticatorProvider;
	}

	protected IProviderGetter<IAuthenticator> getAuthenticatorProvider() {
		return this.authenticatorProvider;
	}

	public void setProductValidator(IProductValidator productValidator) {
		this.productValidator = productValidator;
	}

	public void setInvalidCodeHandler(IActivationInvalidCodeHandler invalidCodeHandler) {
		this.invalidCodeHandler = invalidCodeHandler;
	}

	public void setProductFinalizer(IProductFinalizerBehavior productFinalizer) {
		this.productFinalizer = productFinalizer;
	}

	public void setAuthenticatorModeProvider(AuthenticatorModeProvider authenticatorModeProvider) {
		this.authenticatorModeProvider = authenticatorModeProvider;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvder) {
		this.productionConfigProvider = productionConfigProvder;
	}
}
