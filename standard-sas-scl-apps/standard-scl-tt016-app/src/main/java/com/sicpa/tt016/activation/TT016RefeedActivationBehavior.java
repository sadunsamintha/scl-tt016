package com.sicpa.tt016.activation;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.ActivationException;
import com.sicpa.standard.sasscl.business.activation.impl.AbstractActivationBehavior;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.standard.sasscl.model.ProductStatus.REFEED;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE_MSG_CODE;
import static com.sicpa.tt016.model.TT016ProductStatus.REFEED_NO_INK;

public class TT016RefeedActivationBehavior extends AbstractActivationBehavior {

    private static final Logger logger = LoggerFactory.getLogger(TT016RefeedActivationBehavior.class);

    private BlobDetectionUtils blobDetectionUtils;

    @Override
    public Product receiveCode(Code code, boolean isValid) throws ActivationException {
        logger.debug("Code received = {} , Is good code = {}", code, isValid);

        Product product = new Product();
        product.setCode(code);
        product.setSku(productionParameters.getSku());
        product.setStatus(REFEED);

        if (isCodeNoInk(code, isValid)) {
            logger.warn(NO_INK_IN_REFEED_MODE_MSG_CODE + " "  + Messages.get("business.activation.refeed.noink"));
            EventBusService.post(new MessageEvent(NO_INK_IN_REFEED_MODE));
            product.setStatus(REFEED_NO_INK);
        }

        return product;
    }

    private boolean isCodeNoInk(Code code, boolean isValid) {
        return !isValid && !blobDetectionUtils.isBlobDetected(code);
    }

    public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
        this.blobDetectionUtils = blobDetectionUtils;
    }
}
