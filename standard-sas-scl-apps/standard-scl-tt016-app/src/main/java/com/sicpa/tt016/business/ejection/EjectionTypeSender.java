package com.sicpa.tt016.business.ejection;

import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.PlcParamSender;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EjectionTypeSender {

    private PlcParamSender plcParamSender;

    private static final String KEY_LOGICAL_PLC_PARAM_LINE_EJECTION_TYPE = "PARAM_LINE_EJECTION_TYPE";

    private static final String KEY_DEFAULT_EJECTION_TYPE = "productionmode.DEFAULT";

    private Map<String, String> overrideParameters = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(EjectionTypeSender.class);

    public void send(ProductionParameters pp) {
        ProductionMode mode = pp.getProductionMode();
        for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
            try {
                String ejectionType = Optional.ofNullable(overrideParameters.get(mode.getDescription())).orElse(overrideParameters.get(KEY_DEFAULT_EJECTION_TYPE));
                if (ejectionType != null && !ejectionType.isEmpty()) {
                    plcParamSender.sendToPlcAndReloadParams(KEY_LOGICAL_PLC_PARAM_LINE_EJECTION_TYPE, ejectionType, lineIndex);
                    logger.info("ejection_type_sent_to_plc,mode=" + mode.getDescription() + ",line=" + lineIndex + ",ejection_type=" + ejectionType);
                } else {
                    logger.info("ejection_type_is_not_defined,mode=" + mode.getDescription() + ",line=" + lineIndex);
                }
            } catch (Exception ex) {
                logger.error("Error sending variable to PLC,line=" + lineIndex + ",exception=" + ex.getMessage());
            }
        }
    }

    public void setPlcParamSender(PlcParamSender plcParamSender) {
        this.plcParamSender = plcParamSender;
    }

    public void setOverrideParameters(Map<String, String> overrideParameters) {
        this.overrideParameters = overrideParameters;
    }
}
