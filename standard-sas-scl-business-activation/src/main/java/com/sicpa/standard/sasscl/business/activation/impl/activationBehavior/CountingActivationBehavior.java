package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class CountingActivationBehavior extends AbstractActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(CountingActivationBehavior.class);

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);

		Product p = new Product();
		p.setStatus(ProductStatus.COUNTING);

		if (productionParameters.getSku() != null) {
			p.setSku(productionParameters.getSku());
		}
		return p;
	}
}
