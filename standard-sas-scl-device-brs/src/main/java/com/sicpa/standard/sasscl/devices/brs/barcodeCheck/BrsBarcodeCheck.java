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
import com.sicpa.standard.sasscl.model.SKU;



public class BrsBarcodeCheck {

    private static final Logger logger = LoggerFactory.getLogger(BrsBarcodeCheck.class);

    private Set<String> validBarcodes = new HashSet<>();

    private CompliantProduct compliantProductResolver;

    private boolean isSkuSelectedCompliantProduct = true;
    
    private ApplicationFlowState currentState;
    
    private SKU selectedSKU;

    public BrsBarcodeCheck() {
    	
    }

    public BrsBarcodeCheck(Set<String> validBarcodes) {
        this.validBarcodes = validBarcodes;
    }

    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        this.validBarcodes.clear();
        this.selectedSKU = evt.getProductionParameters().getSku();
        
        if (this.selectedSKU != null) {
            logger.info("Setting BRS Valid Barcodes:" + this.selectedSKU.getBarCodes());
            
            for (String barcode : this.selectedSKU.getBarCodes()) {
            	this.validBarcodes.add(barcode.trim());
			}
            
            isSkuSelectedCompliantProduct = compliantProductResolver.isCompliant(this.selectedSKU);
        }
    }

    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent evt) {
    	if (currentState != null && currentState.equals(ApplicationFlowState.STT_STARTED)) {
    		String brsReceivedBarcode = "";
    		if (null != evt.getCode()) {
    			brsReceivedBarcode = evt.getCode().trim();
    		}
    		logger.debug("BRS Code Received:" + brsReceivedBarcode);
    		
    		String selectedSkuBarcode = "";
    		if (null != selectedSKU.getBarCodes() && selectedSKU.getBarCodes().size() > 0) {
    			selectedSkuBarcode = selectedSKU.getBarCodes().get(0).trim();
    		}
    		logger.debug("Selected SKU Barcode:" + selectedSkuBarcode);
        	
            if (isSkuSelectedCompliantProduct) {
            	if (validBarcodes.size() > 0 && !validBarcodes.contains(brsReceivedBarcode)) {
                    logger.info("Wrong SKU Detected, expected: {} , read: {}", validBarcodes, brsReceivedBarcode);
                    EventBusService.post(new BrsWrongBarcodeEvent(new ArrayList<String>(validBarcodes), brsReceivedBarcode));
                } else if (selectedSkuBarcode.contains("NOBARCODE") && !brsReceivedBarcode.contains("NOBARCODE")) {
                	logger.info("Wrong SKU Detected, NOBARCODE is expected: {} , read: {}", validBarcodes, brsReceivedBarcode);
                    EventBusService.post(new BrsWrongBarcodeEvent(new ArrayList<String>(validBarcodes), brsReceivedBarcode));
                } else if ((validBarcodes.size() == 0 || "".equals(selectedSkuBarcode)) && !"".equals(brsReceivedBarcode)) {
                	noExpectedBarcode(brsReceivedBarcode);
                }
            } else {
            	if ((validBarcodes.size() == 0 || "".equals(selectedSkuBarcode)) && !"".equals(brsReceivedBarcode)) {
            		noExpectedBarcode(brsReceivedBarcode);
                }
            }
		}
    }
    
    private void noExpectedBarcode(String brsReceivedBarcode) {
    	logger.info("Wrong SKU Detected, no expected barcode, read: {}", brsReceivedBarcode);
        EventBusService.post(new BrsWrongBarcodeEvent(new ArrayList<String>(), brsReceivedBarcode));
    }
    
    @Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
    	currentState = evt.getCurrentState();
	}

    public void setCompliantProductResolver(CompliantProduct compliantProductResolver) {
        this.compliantProductResolver = compliantProductResolver;
    }
}
