package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import static com.sicpa.standard.sasscl.model.ProductStatus.AUTHENTICATED;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;

public class NoProductValidator extends AbstractProductValidator {

	@Override
	public void validate(Product product, DecodedCameraCode cameraCode) {
		product.setStatus(AUTHENTICATED);
	}

}