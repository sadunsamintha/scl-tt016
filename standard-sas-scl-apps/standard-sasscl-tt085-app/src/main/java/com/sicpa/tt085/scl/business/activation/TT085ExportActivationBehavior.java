package com.sicpa.tt085.scl.business.activation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

/**
 * If a valid code is received it will send a <code>ProductionRuntimeException</code> and thus stop the production<br/>
 * if a invalid code or blob detection is received it will set the product status to <code>ProductStatus.EXPORT</code>
 */
public class TT085ExportActivationBehavior extends ExportActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(TT085ExportActivationBehavior.class);

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);
		Product p = new Product();
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
