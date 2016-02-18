package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsWrongBarcodeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to trigger a wrong SKU error if a number of wrong barcodes is bigger than the define threshold .
 */
public class BrsWrongBarcodeThreshold {

    private static final Logger logger = LoggerFactory.getLogger(BrsWrongBarcodeThreshold.class);

    private final boolean isThresholdActive;

    private  final int wrongSKUThreshold;

    private  final BrsWindow window;

    public BrsWrongBarcodeThreshold(boolean isThresholdActive, int wrongSKUThreshold, BrsWindow window) {
        this.isThresholdActive = isThresholdActive;
        this.wrongSKUThreshold = wrongSKUThreshold;
        this.window = window;
    }

    @Subscribe
    public void onWrongBarcodeReceived(BrsWrongBarcodeEvent evt) {
        if ( !isThresholdActive ){
            return;
        }

        final int windowCountWrongBarcode = window.incrementAndGetWindowCount();
        if (isWrongBarcodeThresholdReached(windowCountWrongBarcode)) {
            logger.debug("Wrong barcode threshold has been reached");
            triggerWrongBarcodeErrorEvent();
        }
    }

    private boolean isWrongBarcodeThresholdReached(int windowCountWrongBarcode) {
        return windowCountWrongBarcode > wrongSKUThreshold;
    }

    private void triggerWrongBarcodeErrorEvent() {
        EventBusService.post(new MessageEvent(MessageEventKey.BRS.BRS_WRONG_SKU));
    }



}
