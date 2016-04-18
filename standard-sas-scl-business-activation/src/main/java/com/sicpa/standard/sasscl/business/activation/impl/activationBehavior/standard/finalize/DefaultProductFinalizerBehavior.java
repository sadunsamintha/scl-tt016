package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_CODE_TYPE_MISMATCH;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_NOT_AUTHENTICATED;
import static com.sicpa.standard.sasscl.model.ProductStatus.NOT_AUTHENTICATED;
import static com.sicpa.standard.sasscl.model.ProductStatus.TYPE_MISMATCH;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.model.Product;

/**
 * Check the status of the product and throw a exception is the status is <code>ProductStatus.NOT_AUTHENTICATED</code>
 * or <code>ProductStatus.TYPE_MISMATCH</code>
 */
public class DefaultProductFinalizerBehavior implements IProductFinalizerBehavior {

	/**
	 * Check the status of the product and throw a exception is the status is
	 * <code>ProductStatus.NOT_AUTHENTICATED</code> or <code>ProductStatus.TYPE_MISMATCH</code>
	 */
	public DefaultProductFinalizerBehavior() {
	}

	@Override
	public void finalize(Product product) {

		if (product != null && product.getStatus() != null) {
			if (product.getStatus().equals(TYPE_MISMATCH)) {
				EventBusService.post(new MessageEvent(EXCEPTION_CODE_TYPE_MISMATCH));
			} else if (product.getStatus().equals(NOT_AUTHENTICATED)) {
				EventBusService.post(new MessageEvent(EXCEPTION_NOT_AUTHENTICATED));
			}
		}
	}
}
