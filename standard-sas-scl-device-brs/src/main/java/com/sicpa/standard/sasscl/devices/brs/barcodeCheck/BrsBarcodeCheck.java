package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsWrongBarcodeEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;



public class BrsBarcodeCheck {

    private static final Logger logger = LoggerFactory.getLogger(BrsBarcodeCheck.class);


    private Set<String> validBarcodes = new HashSet<>();

    private CompliantProduct compliantProductResolver;

    private boolean isSkuSelectedCompliantProduct = true;
    
    private ApplicationFlowState currentState;

    public BrsBarcodeCheck() {
    }

    public BrsBarcodeCheck(Set<String> validBarcodes) {
        this.validBarcodes = validBarcodes;
    }

    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        this.validBarcodes.clear();
        if(evt.getProductionParameters().getSku() !=  null) {
            logger.info("Setting BRS Valid Barcodes:" + evt.getProductionParameters().getSku().getBarCodes());
            this.validBarcodes.addAll(evt.getProductionParameters().getSku().getBarCodes());
            isSkuSelectedCompliantProduct = compliantProductResolver.isCompliant(evt.getProductionParameters().getSku());
        }
    }

    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent evt) {
    	if (currentState != null && currentState.equals(ApplicationFlowState.STT_STARTED)) {
    		if(!isSkuSelectedCompliantProduct)
                return; // do nothing

            logger.debug("BRS Code Received:" + evt.getCode());

            if(!validBarcodes.contains(evt.getCode())) {
                logger.info("Wrong SKU Detected, expected: {} , read: {}", validBarcodes, evt.getCode() );
                EventBusService.post(new BrsWrongBarcodeEvent(new ArrayList<String>(validBarcodes), evt.getCode()));
            }
		}
    }
    
    @Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
    	currentState = evt.getCurrentState();
	}

    public void setCompliantProductResolver(CompliantProduct compliantProductResolver) {
        this.compliantProductResolver = compliantProductResolver;
    }

}
