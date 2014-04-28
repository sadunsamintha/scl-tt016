package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

/**
 * Check the status of the product and throw a exception is the status is <code>ProductStatus.NOT_AUTHENTICATED</code>
 * or <code>ProductStatus.TYPE_MISMATCH</code>
 * 
 * @author DIelsch
 * 
 */
public class DefaultProductFinalizerBehavior implements IProductFinalizerBehavior {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProductFinalizerBehavior.class);

	/**
	 * Check the status of the product and throw a exception is the status is
	 * <code>ProductStatus.NOT_AUTHENTICATED</code> or <code>ProductStatus.TYPE_MISMATCH</code>
	 */

	public DefaultProductFinalizerBehavior() {
	}

	@Override
	public void finalize(final Product product) {

		if (product != null && product.getStatus() != null) {
			logger.debug("Finalize product = {}", product);
			if (product.getStatus().equals(ProductStatus.TYPE_MISMATCH)) {
				EventBusService.post(new MessageEvent(MessageEventKey.Activation.EXCEPTION_CODE_TYPE_MISMATCH));
			} else if (product.getStatus().equals(ProductStatus.NOT_AUTHENTICATED)) {
				EventBusService.post(new MessageEvent(MessageEventKey.Activation.EXCEPTION_NOT_AUTHENTICATED));
			}
		}
	}
}
