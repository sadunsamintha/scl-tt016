package com.sicpa.tt065.devices.brs;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.barcodeCheck.BrsBarcodeCheck;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsWrongBarcodeEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SKU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TT065BrsBarcodeCheck extends BrsBarcodeCheck {

    private static final Logger logger = LoggerFactory.getLogger(BrsBarcodeCheck.class);

    private Set<String> validBarcodes = new HashSet<>();

    private CompliantProduct compliantProductResolver;

    private boolean isSkuSelectedCompliantProduct = true;

    private SKU selectedSKU;

    public TT065BrsBarcodeCheck() {
    }

    public TT065BrsBarcodeCheck(Set<String> validBarcodes) {
        this.validBarcodes = validBarcodes;
    }

    @Override
    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        this.validBarcodes.clear();
        if(evt.getProductionParameters().getSku() !=  null) {
            logger.info("Setting BRS Valid Barcodes:" + evt.getProductionParameters().getSku().getBarCodes());
            this.validBarcodes.addAll(evt.getProductionParameters().getSku().getBarCodes());
            isSkuSelectedCompliantProduct = compliantProductResolver.isCompliant(evt.getProductionParameters().getSku());
        }
    }
    @Override
    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent evt) {
        if(!isSkuSelectedCompliantProduct)
            return; // do nothing

        String camereReceivedCode = evt.getCode();

        logger.debug("BRS Code Received:" + camereReceivedCode);

        String seletectedSKUBarCode = selectedSKU.getBarCodes().get(0);

        if ((seletectedSKUBarCode.contains("NOBARCODE") && (!camereReceivedCode.contains("NOBARCODE"))) || !validBarcodes.contains(camereReceivedCode)){
            logger.info("Wrong SKU Detected, expected: {} , read: {}", validBarcodes, camereReceivedCode);
            EventBusService.post(new BrsWrongBarcodeEvent(new ArrayList<>(validBarcodes), camereReceivedCode));
            triggerWrongBarcodeErrorEvent();
        }
    }

    @Override
    public void setCompliantProductResolver(CompliantProduct compliantProductResolver) {
        this.compliantProductResolver = compliantProductResolver;
    }

    private void triggerWrongBarcodeErrorEvent() {
        EventBusService.post(new MessageEvent(MessageEventKey.BRS.BRS_WRONG_SKU));
    }

    @Subscribe
    public void handleProductionParameterChanged(ProductionParametersEvent evt) {
        this.selectedSKU = evt.getProductionParameters().getSku();
    }
}
