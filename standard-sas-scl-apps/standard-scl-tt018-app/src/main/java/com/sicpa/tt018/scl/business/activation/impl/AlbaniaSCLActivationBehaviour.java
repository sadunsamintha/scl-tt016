package com.sicpa.tt018.scl.business.activation.impl;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.scl.model.AlbaniaProduct;

public class AlbaniaSCLActivationBehaviour extends StandardActivationBehavior {

	@Override
	protected Product getNewProduct() {
		return new AlbaniaProduct(ProductionMode.STANDARD);
	}

}
