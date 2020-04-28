package com.sicpa.tt016.devices.plc.impl;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.devices.plc.PlcValuesLoader;

public class TT016PlcLoader extends PlcValuesLoader {

    private static final Logger logger = LoggerFactory.getLogger(TT016PlcLoader.class);

    private boolean isWiperEnabled;
    private boolean isAutomatedBeamEnabled;

    @Override
    protected StringMap loadLineProperties(int lineIndex) throws IOException {
        String file = getLinePropertiesFileName(lineIndex);
        StringMap res = loadProperties(file);
        res = replaceLinePlaceholderInProperties(lineIndex, res);

        if (!isAutomatedBeamEnabled) {
            res.remove("PARAM_CONVEYOR_HEIGHT_FROM_FLOOR_MM");
            res.remove("PARAM_PRODUCT_TO_CAMERA_HEIGHT_MM");
            res.remove("REQUEST_BEAM_MANUAL_MODE");
        }
        if (!isWiperEnabled) {
            res.remove("PARAM_LINE_INHIBIT_WIPER");
            res.remove("PARAM_LINE_WIPER_DISTANCE");
            res.remove("PARAM_LINE_WIPER_LENGTH");
            res.remove("PARAM_LINE_WIPER_RATIOENCODERMOTOR");
        }

        return res;
    }

    public void setWiperEnabled(boolean wiperEnabled) {
        isWiperEnabled = wiperEnabled;
    }

    public void setAutomatedBeamEnabled(boolean automatedBeamEnabled) {
        isAutomatedBeamEnabled = automatedBeamEnabled;
    }
}
