package com.sicpa.tt018.scl.business.activation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.scl.model.AlbaniaProduct;

public class AlbaniaExportActivationBehavior extends ExportActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaExportActivationBehavior.class);

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);
		AlbaniaProduct p = new AlbaniaProduct(ProductionMode.EXPORT);
		if (valid) {
			handleGoodCodes(p, code);
		} else {
			p.setStatus(ProductStatus.EXPORT);
		}
		if (productionParameters.getSku() != null) {
			p.setSku(productionParameters.getSku());
		}
		return p;
	}
}
