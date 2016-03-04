import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesLoader
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.PlcPulseToMMConverterHandler
import com.sicpa.standard.sasscl.devices.plc.remoteserver.PlcRemoteServerConnectionHandler
import com.sicpa.standard.sasscl.devices.plc.PlcStateListener
import com.sicpa.standard.sasscl.devices.plc.warningerror.PlcRegisterHandler
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor
import com.sicpa.standard.sasscl.devices.plc.PlcJmxInfo
import static com.sicpa.standard.sasscl.messages.MessageEventKey.PLC.*

import com.sicpa.standard.client.common.ioc.InjectByMethodBean;

beans{

	plcValuesLoader(PlcValuesLoader)

	plcPulseToMMConverterHandler(PlcPulseToMMConverterHandler){
		encoderModFolEvalVarName='.stParameters.nEncoderModuleFoldEvaluation'
		shapeDiameterVarName='.stParameters.nShapeDiameter'
		encoderResolutionVarName='.stParameters.nEncoderResolution'
	}

	plcRemoteServerConnectionHandler(PlcRemoteServerConnectionHandler){
		remoteServer=ref('remoteServer')
		plcProvider=ref('plcProvider')
		reqJavaErrorRegisterVar= ref('REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER_var')
	}

	plcStateListener(PlcStateListener){
		plcProvider=ref('plcProvider')
		lineStateVarName="#{plcVarMap['NTF_LINE_STATE']}"
	}

	plcRegisterHandler(PlcRegisterHandler) {
		plcProvider=ref('plcProvider')
		cabRegisterVarName="#{plcVarMap['NTF_CAB_WAR_ERR_REGISTER']}"
		lineRegisterVarName="#{plcVarMap['NTF_LINE_WAR_ERR_REGISTER']}"
	}


	plc(PlcAdaptor,ref('plcController')){b->
		b.scope='prototype'
		notificationVariables=ref('plcCabinetNtf')
		plcRequestActionMap=ref('mapPlcRequestAction')
		parameters=ref('plcCabinetParameters')
		loader=ref('plcValuesLoader')
		parameterLine=ref('plcLineParamsTemplate')
		notificationLine=ref('plcLineNtfTemplate')
		lineVarGroups=ref('linePlcVarGroup')
		plcConfigFolder=profilePath+'/config/plc'

		lineSpeedVarName="#{plcVarMap['NTF_LINE_SPEED']}"
		productFreqVarName="#{plcVarMap['NTF_LINE_PRODS_PER_SECOND']}"
		systemTypeVarName="#{plcVarMap['PARAM_LINE_SYSTEM_TYPE']}"
		lineActiveVarName="#{plcVarMap['PARAM_LINE_IS_ACTIVE']}"
		plcVersionHVarName="#{plcVarMap['NTF_CAB_VERSION_HIGH']}"
		plcVersionMVarName="#{plcVarMap['NTF_CAB_VERSION_MEDIUM']}"
		plcVersionLVarName="#{plcVarMap['NTF_CAB_VERSION_LOW']}"
	}

	plcJmxInfo(PlcJmxInfo){
		plcProvider=ref('plcProvider')
		plcCabinetVars=ref('plcCabJmxReport')
		plcLineVars=ref('plcLineJmxReport')
	}


	def cabConfigFile=profilePath+'/config/plc/cabinetConfig.xml'

	allPlcVariableValues(ConfigUtils,cabConfigFile){b->
		b.factoryMethod='load'
	}

	cabinetEditablePlcVariables(EditablePlcVariables,ref('allPlcVariableValues'),ref('cabPlcVarGroups')){ file=cabConfigFile }


	def cab_msg=[
		PLC_WAR_JAVA_WARNING,
		PLC_WAR_JAVA_CRITICAL_WARNING,
		PLC_WAR_COOLING_NOT_ACTIVATED,
		PLC_WAR_SOFTWARE_VERSION_FAULT,
		PLC_WAR_PLC_TEMPERATURE_TO_HIGH,
		PLC_WAR_TEMPERATURE_EE_CABINET,
		PLC_WAR_TEMPERATURE_AMBIANT,
		PLC_WAR_RELATIVE_HUMIDITY_DEFAULT,
		PLC_WAR_POWER_SUPPLY_DEFAULT,
		PLC_WAR_BREAKER_CONTROL_DEFAULT,
		PLC_WAR_AIR_PRESSURE_DEFAULT,
		PLC_WAR_DOOR_SWITCH_OCS_OPEN,
		PLC_WAR_DOOR_SWITCH_EE_OPEN,
		PLC_WAR_COOLING_FAN1_EE_CAB_DEFAULT,
		PLC_WAR_COOLING_FAN2_EE_CAB_DEFAULT,
		PLC_WAR_PLC_FAULT,
		PLC_ERR_JAVA_ERROR,
		PLC_ERR_LIFE_CHECK,
		PLC_ERR_PLC_FAULT,
		PLC_ERR_COOLING_NOT_ACTIVATED,
		PLC_ERR_PLC_TEMPERATURE_TOO_HIGH,
		PLC_ERR_EMERGENCY_STOP,
		PLC_ERR_TEMPERATURE_EE_CABINET,
		PLC_ERR_TEMPERATURE_AMBIANT,
		PLC_ERR_POWER_SUPPLY_DEFAULT,
		PLC_ERR_BREAKER_CONTROL_DEFAULT,
		PLC_ERR_AIR_PRESSURE_DEFAULT,
		PLC_ERR_DOOR_SWITCH_OCS_OPEN,
		PLC_ERR_DOOR_SWITCH_EE_OPEN
	]

	setCabinetWarningAndErrors(InjectByMethodBean){
		target = ref('plcRegisterHandler')
		methodName ='setCabinetErrorsList'
		param = [cab_msg]
	}

	def line_msg=[
		PLC_WAR_TRIGGER_SHIFT_FLAG,
		null,
		PLC_WAR_INVALID_PARAMETER,
		PLC_WAR_LABEL_APP_OR_AIR_DRYER_WARNING,
		PLC_WAR_DRS_ACQUISITION_ERROR,
		PLC_WAR_DRS_UNKNOWN_ANSWER,
		PLC_WAR_DRS_FIFOS_FAULT,
		PLC_WAR_DRS_NOT_CONNECTED,
		PLC_WAR_ENCODER_FAULT,
		PLC_WAR_TEMPERATURE_IJ_CABINET,
		PLC_WAR_TEMPERATURE_IJ_INK,
		PLC_WAR_EXT_AIR_DRYER_WARNING,
		PLC_WAR_DOOR_SWITCH_IJ_OPEN,
		PLC_WAR_COOLING_FAN1_PRINTER_DEFAULT,
		PLC_WAR_COOLING_FAN2_PRINTER_DEFAULT,
		null,
		PLC_ERR_TRIGGER_FAULT,
		PLC_ERR_MNFCT_LINE_ERROR,
		PLC_ERR_INVALID_PARAMETER,
		PLC_ERR_LABEL_APP_OR_AIR_DRYER_NOT_READY,
		PLC_ERR_LABEL_APP_OR_AIR_DRYER_FAULT,
		PLC_ERR_UNKNOWN_DRS,
		PLC_ERR_DRS_NOT_CONNECTED,
		PLC_ERR_TOO_CONSECUTIVE_INVALID_CODES,
		PLC_ERR_EJECTION_SMALL_RATE_TOO_HIGH,
		PLC_ERR_EJECTION_LARGE_RATE_TOO_HIGH,
		PLC_ERR_VALID_CODE_IN_EXPORT_MODE,
		PLC_ERR_TEMPERATURE_IJ_CABINET,
		PLC_ERR_TEMPERATURE_IJ_INK,
		PLC_ERR_DOOR_SWITCH_IJ_OPEN
	]

	setlineWarningAndErrors(InjectByMethodBean){
		target = ref('plcRegisterHandler')
		methodName ='setLineErrorsList'
		param = [line_msg]
	}
}




