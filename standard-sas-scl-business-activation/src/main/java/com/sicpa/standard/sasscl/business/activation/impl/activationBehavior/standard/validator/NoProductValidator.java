package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class NoProductValidator extends AbstractProductValidator {

	private static final Logger logger = LoggerFactory.getLogger(NoProductValidator.class);

	@Override
	public void validate(Product product, DecodedCameraCode cameraCode) {
		product.setStatus(ProductStatus.AUTHENTICATED);
	}

}