package com.sicpa.standard.sasscl.devices.brs.skuCheck;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;



public class BrsSkuCheck {

    private static final Logger logger = LoggerFactory.getLogger(BrsSkuCheck.class);


    private Set<String> validBarcodes = new HashSet<>();

    private CompliantProduct compliantProductResolver;

    private boolean isSkuSelectedCompliantProduct = true;


    public BrsSkuCheck() {
    }

    public BrsSkuCheck(Set<String> validBarcodes) {
        this.validBarcodes = validBarcodes;
    }

    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        logger.info("Setting BRS Valid Barcodes:" + evt.getProductionParameters().getSku().getBarCodes());

        this.validBarcodes.clear();
        this.validBarcodes.addAll(evt.getProductionParameters().getSku().getBarCodes());
        isSkuSelectedCompliantProduct = compliantProductResolver.isCompliant(evt.getProductionParameters().getSku());
    }

    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent evt) {
        if(!isSkuSelectedCompliantProduct)
            return; // do nothing

        logger.debug("BRS Code Received:" + evt.getCode());

        if(!validBarcodes.contains(evt.getCode())) {
            logger.info("Wrong SKU Detected, expected: {} , read: {}", validBarcodes, evt.getCode() );
            EventBusService.post(new MessageEvent(MessageEventKey.BRS.BRS_WRONG_SKU));
        }
    }

    public void setCompliantProductResolver(CompliantProduct compliantProductResolver) {
        this.compliantProductResolver = compliantProductResolver;
    }

}
