package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.ActivationException;
import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

/**
 * If a valid code is received it will send a <code>ProductionRuntimeException</code> and thus stop the production<br/>
 * if a invalid code is received it will set the product status to <code>ProductStatus.EXPORT</code>
 * 
 * @author DIelsch
 * 
 */
public class ExportActivationBehavior extends AbstractActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(ExportActivationBehavior.class);

	@Override
	public Product receiveCode(final Code code, final boolean valid) throws ActivationException {
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

	protected void handleGoodCodes(Product product, Code code) {
		product.setStatus(ProductStatus.UNREAD);
		product.setCode(code);
		EventBusService.post(new MessageEvent(MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT));
	}
}
