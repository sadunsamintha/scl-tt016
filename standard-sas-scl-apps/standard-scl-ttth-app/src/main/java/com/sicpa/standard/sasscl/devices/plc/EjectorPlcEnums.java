package com.sicpa.standard.sasscl.devices.plc;

public enum EjectorPlcEnums {

	PARAM_LINE_EJECT_CONFIRM_TYPE(".com.stLine[#x].stParameters.bEjectConfirmType", PlcUtils.PLC_TYPE.B),
	PARAM_LINE_INHIBIT_EJECT_CONFIRM_EJECT(".com.stLine[#x].stParameters.bInhibitEjectConfirm_Eject", PlcUtils.PLC_TYPE.B),
	PARAM_LINE_INHIBIT_EJECT_CONFIRM_PASS(".com.stLine[#x].stParameters.bInhibitEjectConfirm_Pass", PlcUtils.PLC_TYPE.B),
	PARAM_LINE_EJECT_CONFIRM_DISTANCE(".com.stLine[#x].stParameters.nEjectConfirmDistance", PlcUtils.PLC_TYPE.I),
	PARAM_LINE_EJECT_CONFIRM_LENGTH(".com.stLine[#x].stParameters.nEjectConfirmLength", PlcUtils.PLC_TYPE.I),
	PARAM_LINE_EJECT_TABLE_FULL_MILLISECOND(".com.stLine[#x].stParameters.nEjectTableFullMillisecond", PlcUtils.PLC_TYPE.I),
	PARAM_LINE_EJECT_TABLE_PARTIAL_FULL_MILLISECOND(".com.stLine[#x].stParameters.nEjectTablePartialFullMillisecond", PlcUtils.PLC_TYPE.I),
	PARAM_LINE_INHIBIT_PRODUCTION_STOP_EJECTION(".com.stLine[#x].stParameters.bInhibitProductionStopEjection", PlcUtils.PLC_TYPE.B);

	private String nameOnPlc;
	private PlcUtils.PLC_TYPE plc_type;

	EjectorPlcEnums(String nameOnPlc, PlcUtils.PLC_TYPE plc_type) {
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
