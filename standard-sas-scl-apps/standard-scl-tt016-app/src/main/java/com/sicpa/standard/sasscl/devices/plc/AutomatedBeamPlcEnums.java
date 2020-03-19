package com.sicpa.standard.sasscl.devices.plc;

public enum AutomatedBeamPlcEnums {

	//This is used to set the product height variable for the beam
	PARAM_PRODUCT_HEIGHT_MM(".com.stLine[#x].stParameters.nProduct_Height", PlcUtils.PLC_TYPE.I),

	//This is used to set the conveyor height variable for the beam
	PARAM_CONVEYOR_HEIGHT_FROM_FLOOR_MM(".com.stLine[#x].stParameters.nConveyor_Height", PlcUtils.PLC_TYPE.I),

	//This is used to tell the beam to adjust to set height
	REQUEST_BEAM_HEAD_TO_HEIGHT(".com.stLine[#x].stRequests.diFlag_Head_To_Position", PlcUtils.PLC_TYPE.B),

	//This flag is used to block starting of production while beam is adjusting
	REQUEST_BEAM_ADJUSTING_HEIGHT(".com.stLine[#x].stRequests.diFlag_Adjusting_Height", PlcUtils.PLC_TYPE.B),

	//This is used for alarm flag to signal if alarm is triggered
	REQUEST_SAFETY_SENSOR_TRIG(".com.stLine[#x].stRequests.diFlag_Alarm_Beam", PlcUtils.PLC_TYPE.B),

	//This used for invalid height flag if conveyor and product height exceeds beam's threshold.
	BEAM_INVALID_HEIGHT_DETECTED_FLAG(".com.stLine[#x].stRequests.diFlag_Invalid_Height", PlcUtils.PLC_TYPE.B);

	private String nameOnPlc;
	private PlcUtils.PLC_TYPE plc_type;

	AutomatedBeamPlcEnums(String nameOnPlc, PlcUtils.PLC_TYPE plc_type) {
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
