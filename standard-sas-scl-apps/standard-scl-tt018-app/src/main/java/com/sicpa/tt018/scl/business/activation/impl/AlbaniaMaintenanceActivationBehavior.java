package com.sicpa.tt018.scl.business.activation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.scl.model.AlbaniaProduct;

public class AlbaniaMaintenanceActivationBehavior extends AbstractActivationBehavior {

	private static Logger logger = LoggerFactory.getLogger(AlbaniaMaintenanceActivationBehavior.class);

	@Override
	public Product receiveCode(final Code code, final boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);

		AlbaniaProduct p = new AlbaniaProduct(ProductionMode.MAINTENANCE);
		p.setStatus(ProductStatus.MAINTENANCE);
		p.setCode(code);
		return p;
	}
}
