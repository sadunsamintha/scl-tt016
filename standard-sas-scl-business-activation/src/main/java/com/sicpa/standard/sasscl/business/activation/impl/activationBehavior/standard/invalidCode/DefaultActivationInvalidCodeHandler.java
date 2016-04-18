package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode;

import static com.sicpa.standard.sasscl.model.ProductStatus.UNREAD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.Product;

/**
 * Set the product status to <code>setStatus(ProductStatus.UNREAD)</code>
 */
public class DefaultActivationInvalidCodeHandler implements IActivationInvalidCodeHandler {

	private static final Logger logger = LoggerFactory.getLogger(DefaultActivationInvalidCodeHandler.class);

	@Override
	public void handleInvalidCode(Product product) {
		logger.debug("Invalid Code: {}", product);
		product.setStatus(UNREAD);
	}
}
