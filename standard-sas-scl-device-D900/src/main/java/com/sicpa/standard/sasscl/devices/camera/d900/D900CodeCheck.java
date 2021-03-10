package com.sicpa.standard.sasscl.devices.camera.d900;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.d900.sku.D900CompliantProduct;
import com.sicpa.standard.sasscl.devices.d900.D900CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.d900.D900CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.d900.D900CameraNewCodeEvent;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.std.bis2.core.domain.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class D900CodeCheck {
    private static final Logger logger = LoggerFactory.getLogger(D900CodeCheck.class);

    private String validCode = "";

    private D900CompliantProduct d900CompliantProductResolver;

    private boolean isSkuSelectedCompliantProduct = true;

    private ApplicationFlowState currentState;

    private SKU selectedSKU;

    public D900CodeCheck() {
    }

    public D900CodeCheck(String validCode) {
        this.validCode = validCode;
    }

    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        this.validCode= new String("");
        this.selectedSKU = evt.getProductionParameters().getSku();

        if (this.selectedSKU != null) {
            logger.info("Setting Vidi Valid SKU:" + this.selectedSKU.getBarCodes());
            this.validCode = selectedSKU.getDescription().trim();

            isSkuSelectedCompliantProduct = d900CompliantProductResolver.isCompliant(this.selectedSKU);
        }
    }

    @Subscribe
    public void onD900ValidCodeReceived(D900CameraNewCodeEvent evt) {
        if (currentState != null && currentState.equals(ApplicationFlowState.STT_STARTED)) {
            String d900ReceivedCode = "";
            if (null != evt.getCode()) {
                d900ReceivedCode = evt.getCode().trim();
            }
            logger.debug("D900 Code Received:" + d900ReceivedCode);

            String selectedSkuCode = "";
            if (null != selectedSKU.getDescription() &&
                    null != selectedSKU.getDescription()) {
                selectedSkuCode = selectedSKU.getDescription().trim();
            }
            logger.debug("Selected SKU Barcode:" + selectedSkuCode);

            if (isSkuSelectedCompliantProduct) {
                if (!validCode.equals(d900ReceivedCode)) {
                    logger.info("Wrong SKU Detected, expected: {} , read: {}", validCode, d900ReceivedCode);
                    EventBusService.post(new D900CameraBadCodeEvent(d900ReceivedCode));

                } else if (selectedSkuCode.contains("NOBARCODE") && !d900ReceivedCode.contains("NOBARCODE")) {
                    logger.info("Wrong SKU Detected, NOBARCODE is expected: {} , read: {}", validCode, d900ReceivedCode);
                    EventBusService.post(new D900CameraBadCodeEvent(d900ReceivedCode));
                } else if ((validCode.equals("") || "".equals(selectedSkuCode)) && !"".equals(d900ReceivedCode)) {
                    noExpectedBarcode(d900ReceivedCode);
                } else
                {
                    EventBusService.post(new D900CameraGoodCodeEvent(d900ReceivedCode));
                }
            } else {
                if ((validCode.equals("")  || "".equals(selectedSkuCode)) && !"".equals(d900ReceivedCode)) {
                    noExpectedBarcode(d900ReceivedCode);
                }
            }
        }
    }

    private void noExpectedBarcode(String d900ReceivedCode) {
        logger.info("Wrong SKU Detected, no expected barcode, read: {}", d900ReceivedCode);
        EventBusService.post(new D900CameraBadCodeEvent(d900ReceivedCode));
    }

    @Subscribe
    public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
        currentState = evt.getCurrentState();
    }

    public void setD900CompliantProductResolver(D900CompliantProduct d900CompliantProductResolver) {
        this.d900CompliantProductResolver = d900CompliantProductResolver;
    }

    public void setCurrentState(ApplicationFlowState currentState){
        this.currentState = currentState;
    }

    public void setSelectedSKU(SKU selectedSKU){
        this.selectedSKU = selectedSKU;
    }
}
