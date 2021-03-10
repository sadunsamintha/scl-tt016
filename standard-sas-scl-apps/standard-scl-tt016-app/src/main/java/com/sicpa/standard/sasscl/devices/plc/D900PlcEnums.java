package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.sasscl.devices.plc.PlcUtils;

public enum  D900PlcEnums {

    PARAM_D900_PRODUCT_COUNT(".com.stLine[#x].stNotifications.nBISProductCount", PlcUtils.PLC_TYPE.I),
    PARAM_D900_CAMERA_OFFLINE_ERROR(".com.stLine[#x].stWarnings.bBISCameraOffLineError", PlcUtils.PLC_TYPE.I),
    PARAM_LINE_BIS_DELAY_TYPE(".com.stLine[#x].stParameters.bBISDelayType", PlcUtils.PLC_TYPE.B),
    PARAM_LINE_BIS_DELAY(".com.stLine[#x].stParameters.nBISDelay", PlcUtils.PLC_TYPE.D),
    PARAM_LINE_BIS_DISTANCE_TYPE(".com.stLine[#x].stParameters.bBISDistanceType", PlcUtils.PLC_TYPE.B),
    PARAM_LINE_BIS_DISTANCE(".com.stLine[#x].stParameters.nBISDistance", PlcUtils.PLC_TYPE.D),
    PARAM_LINE_BIS_LENGTH_TYPE(".com.stLine[#x].stParameters.bBISLengthType", PlcUtils.PLC_TYPE.B),
    PARAM_LINE_BIS_LENGTH(".com.stLine[#x].stParameters.nBISLength", PlcUtils.PLC_TYPE.D);

    private String nameOnPlc;
    private PlcUtils.PLC_TYPE plc_type;

    D900PlcEnums(String nameOnPlc, PlcUtils.PLC_TYPE plc_type) {
        this.nameOnPlc = nameOnPlc;
        this.plc_type = plc_type;
    }

    public String getNameOnPlc() {
        return nameOnPlc;
    }

    public PlcUtils.PLC_TYPE getPlc_type() {
        return plc_type;
    }
}
