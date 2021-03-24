package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.sasscl.devices.plc.PlcUtils;

public enum  D900PlcEnums {

    PARAM_D900_PRODUCT_COUNT(".com.stLine[#x].stNotifications.nBISProductCount", PlcUtils.PLC_TYPE.I),
    PARAM_D900_CAMERA_OFFLINE_ERROR(".com.stLine[#x].stErrors.bBISCameraOffLineError", PlcUtils.PLC_TYPE.B);

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
