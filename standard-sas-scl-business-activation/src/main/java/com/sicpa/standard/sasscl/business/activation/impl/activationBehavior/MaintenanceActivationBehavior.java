package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

/**
 * set the status of the product to ProductStatus.MAINTENANCE
 */
public class MaintenanceActivationBehavior extends AbstractActivationBehavior {

	private static Logger logger = LoggerFactory.getLogger(MaintenanceActivationBehavior.class);

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);

		Product p = new Product();
		p.setStatus(ProductStatus.MAINTENANCE);
		p.setCode(code);
		if (productionParameters.getSku() != null) {
			p.setSku(productionParameters.getSku());
		}
		return p;
	}
}
