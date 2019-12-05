package com.sicpa.tt016.scl.business.activation;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT;
import static com.sicpa.standard.sasscl.model.ProductStatus.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductStatus.UNREAD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class TT016ExportActivationBehavior extends AbstractActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(TT016ExportActivationBehavior.class);

	private BlobDetectionUtils blobDetectionUtils;
	
	private static Code codeExp = new Code("EX");
	private static String exportPrefix = "EX";

	@Override
	public Product receiveCode(Code code, boolean valid) {
		logger.debug("Code received = {} , Is good code = {}", code, valid);
		Product p = new Product();
		if ((valid && !code.getStringCode().startsWith(exportPrefix)) && !blobDetectionUtils.isBlobDetected(code)) {
			handleGoodCodes(p, code);
		} else if((valid && code.getStringCode().startsWith(exportPrefix)) || blobDetectionUtils.isBlobDetected(code)){
			handleGoodCodesExp(p, code);
		}else{
			p.setStatus(ProductStatus.UNREAD);
		}
		
		if (productionParameters.getSku() != null) {
			p.setSku(productionParameters.getSku());
		}
		return p;
	}

	protected void handleGoodCodes(Product product, Code code) {
		product.setStatus(UNREAD);
		product.setCode(code);
		EventBusService.post(new MessageEvent(EXCEPTION_CODE_IN_EXPORT));
	}
	
	protected void handleGoodCodesExp(Product product, Code code) {
		product.setStatus(EXPORT);
		product.setCode(code);
	}

	public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
		this.blobDetectionUtils = blobDetectionUtils;
	}

}
