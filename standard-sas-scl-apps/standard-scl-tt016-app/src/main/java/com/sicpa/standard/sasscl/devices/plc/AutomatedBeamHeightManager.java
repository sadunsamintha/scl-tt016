package com.sicpa.standard.sasscl.devices.plc;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.IssueSolvedMessage;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.tt016.event.AutomatedBeamResetEvent;
import com.sicpa.tt016.scl.model.MoroccoSKU;

import static com.sicpa.standard.sasscl.devices.plc.AutomatedBeamPlcEnums.PARAM_PRODUCT_HEIGHT_MM;
import static com.sicpa.standard.sasscl.devices.plc.AutomatedBeamPlcEnums.REQUEST_BEAM_HEAD_TO_HEIGHT;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEAD_TO_HOME;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEIGHT_SET;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_SAFETY_SENSOR_TRIG;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.SKU_SELECTION_VIEW_ACTIVE;

public class AutomatedBeamHeightManager {

    private static final Logger logger = LoggerFactory.getLogger(AutomatedBeamHeightManager.class);

    private PlcProvider plcProvider;
    private ProductionParameters productionParameters;
    private PlcParamSender plcParamSender;

    private boolean isEnabled;

    private volatile boolean isSafetySensorTriggered = false;

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }

    public void setPlcParamSender(PlcParamSender plcParamSender) {
        this.plcParamSender = plcParamSender;
    }

    public void setEnabled(Boolean enabled) {
        this.isEnabled = enabled;
    }

    public void setBeamHeight() {
        if (productionParameters.getSku() != null && isEnabled && !isSafetySensorTriggered) {
            MoroccoSKU sku = (MoroccoSKU) productionParameters.getSku();
            setBeamHeight(sku.getProductHeight());
        }
    }

    public void setBeamHeight(int value) {
        for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
            if (plcParamSender != null) {
                try {
                    plcParamSender.sendToPlc(PARAM_PRODUCT_HEIGHT_MM.toString(), String.valueOf(value), lineIndex);
                    EventBusService.post(new MessageEvent(AUTOMATED_BEAM_HEIGHT_SET));
                    plcParamSender.sendToPlc(REQUEST_BEAM_HEAD_TO_HEIGHT.toString(), String.valueOf(true), lineIndex);
                } catch (PlcAdaptorException e) {
                    logger.error("Failed to write to PlcVariable {} >> {}",
                        PARAM_PRODUCT_HEIGHT_MM.toString(),
                        value);
                }
            }
        }
    }

    //Send the beam back to SKU height upon operator reset
    @Subscribe
    public void handleBeamReset(AutomatedBeamResetEvent evt) {
        logger.info(evt.message);
        EventBusService.post(new IssueSolvedMessage(AUTOMATED_BEAM_HEAD_TO_HOME, plcProvider.get()));
        isSafetySensorTriggered = false;
        setBeamHeight();
    }

    //Send the beam back to SKU height if at home and SKU is selected
    @Subscribe
    public void handleSkuReselection(MessageEvent evt) {
        if (evt.getKey().equals(SKU_SELECTION_VIEW_ACTIVE)) {
            isSafetySensorTriggered = false;
        }
    }

    //Send the beam to position zero (Home) when safety sensor is triggered
    @Subscribe
    public void handleSafetySensorTrig(MessageEvent evt) {
        if (evt.getKey().equals(AUTOMATED_BEAM_SAFETY_SENSOR_TRIG)) {
            isSafetySensorTriggered = true;
            setBeamHeight(0);
        }
    }
}