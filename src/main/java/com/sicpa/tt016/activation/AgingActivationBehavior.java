package com.sicpa.tt016.activation;

import static com.sicpa.standard.sasscl.model.ProductStatus.UNREAD;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.EXCEPTION_CODE_IN_AGING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.tt016.model.TT016ProductStatus;

/**
 * If a valid code is received it will throw warning<br/>
 * if a invalid code is received it will set the product status to <code>TT016ProductStatus.AGING</code>
 */
public class AgingActivationBehavior extends ExportActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(AgingActivationBehavior.class);

	private BlobDetectionUtils blobDetectionUtils;

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);
		Product p = new Product();
		if (valid || blobDetectionUtils.isBlobDetected(code)) {
			handleGoodCodes(p, code);
		} else {
			p.setStatus(TT016ProductStatus.AGING);
		}
		if (productionParameters.getSku() != null) {
			p.setSku(productionParameters.getSku());
		}
		return p;
	}
	
	@Override
	protected void handleGoodCodes(Product product, Code code) {
		product.setStatus(UNREAD);
		product.setCode(code);
		EventBusService.post(new MessageEvent(EXCEPTION_CODE_IN_AGING));
	}

	public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
		this.blobDetectionUtils = blobDetectionUtils;
	}

}
