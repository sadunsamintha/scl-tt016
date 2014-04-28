package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

/**
 * Set the product status to <code>setStatus(ProductStatus.UNREAD)</code>
 * 
 * @author DIelsch
 * 
 */
public class DefaultActivationInvalidCodeHandler implements IActivationInvalidCodeHandler {

	private static final Logger logger = LoggerFactory.getLogger(DefaultActivationInvalidCodeHandler.class);

	/**
	 * set the product status to unread
	 */
	@Override
	public void handleInvalidCode(final Product product) {
		logger.debug("Invalid Code: {}", product);
		product.setStatus(ProductStatus.UNREAD);
	}
}
