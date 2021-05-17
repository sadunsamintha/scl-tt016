package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class RefeedActivationBehavior extends StandardActivationBehavior {

	@Override
	protected void checkProduct(DecodedCameraCode result, Product product) {
		super.checkProduct(result, product);
		if (ProductStatus.AUTHENTICATED.equals( product.getStatus()) ) {
			product.setStatus(ProductStatus.REFEED);
		}
	}
}