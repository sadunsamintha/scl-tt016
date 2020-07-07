package com.sicpa.standard.sasscl.devices.plc;

public enum AutomatedBeamPlcEnums {

	//This is used to set the product height variable for the beam
	PARAM_PRODUCT_HEIGHT_MM(".com.stLine[#x].stParameters.nProduct_Height", PlcUtils.PLC_TYPE.I),

	//This is used to set the conveyor height variable for the beam
	PARAM_CONVEYOR_HEIGHT_FROM_FLOOR_MM(".com.stLine[#x].stParameters.nConveyor_Height", PlcUtils.PLC_TYPE.I),

	//This is used to toggle the beam to be in manual mode
	REQUEST_BEAM_MANUAL_MODE(".com.stLine[#x].stRequests.diFlag_BeamState", PlcUtils.PLC_TYPE.B),

	//This is used to set the product to camera height variable for the beam
	PARAM_PRODUCT_TO_CAMERA_HEIGHT_MM(".com.stLine[#x].stParameters.ProductToCamera", PlcUtils.PLC_TYPE.I),

	//This is used to monitor the current position of the beam
	REQUEST_BEAM_CURRENT_POSITION_MM(".com.stLine[#x].stParameters.nBeam_Position", PlcUtils.PLC_TYPE.I),

	//This is used to tell the beam to adjust to set height
	REQUEST_BEAM_HEAD_TO_HEIGHT(".com.stLine[#x].stRequests.diFlag_Head_To_Position", PlcUtils.PLC_TYPE.B),

	//This flag is used to block starting of production while beam is adjusting
	REQUEST_BEAM_ADJUSTING_HEIGHT(".com.stLine[#x].stRequests.diFlag_Adjusting_Height", PlcUtils.PLC_TYPE.B),

	//This is used for alarm flag to signal if alarm is triggered
	REQUEST_SAFETY_SENSOR_TRIG(".com.stLine[#x].stRequests.diFlag_Alarm_Beam", PlcUtils.PLC_TYPE.B),

	//This is used for the error state flag
	REQUEST_ERROR_STATE_TRIG(".com.stLine[#x].stRequests.diFlag_ErrorState", PlcUtils.PLC_TYPE.B),

	//This is used for the error state reset acknowledgement
	REQUEST_ERROR_STATE_EXE_TRIG(".com.stLine[#x].stRequests.diFLag_ExcuReset", PlcUtils.PLC_TYPE.B),

	//This is used for invalid height flag if conveyor and product height exceeds beam's threshold.
	REQUEST_INVALID_HEIGHT_DETECTED(".com.stLine[#x].stRequests.diFlag_InvalidHeight", PlcUtils.PLC_TYPE.B),

	//This is used for EStop switch flag if it has been manually pressed by operator.
	REQUEST_EMERGENCY_SWITCH_STATE(".com.stLine[#x].stRequests.diFlag_EmergencySwitch", PlcUtils.PLC_TYPE.B),

	//This is used for starting up the relay link.
	REQUEST_EMERGENCY_LINK_STATE(".com.stLine[#x].stRequests.doFlag_bEmergencyLink", PlcUtils.PLC_TYPE.B);

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
