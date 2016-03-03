import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesLoader
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.PlcPulseToMMConverterHandler
import com.sicpa.standard.sasscl.devices.plc.remoteserver.PlcRemoteServerConnectionHandler
import com.sicpa.standard.sasscl.devices.plc.PlcStateListener
import com.sicpa.standard.sasscl.devices.plc.warningerror.PlcRegisterHandler
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor
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
		notificationLine=ref('plcConveyorNtfTemplate')
		lineVarGroups=ref('lineVarGroups')
		plcConfigFolder=profilePath+'/config/plc'

		lineSpeedVarName="#{plcVarMap['NTF_LINE_SPEED']}"
		productFreqVarName="#{plcVarMap['NTF_LINE_PRODS_PER_SECOND']}"
		systemTypeVarName="#{plcVarMap['PARAM_LINE_SYSTEM_TYPE']}"
		lineActiveVarName="#{plcVarMap['PARAM_LINE_IS_ACTIVE']}"
		plcVersionHVarName="#{plcVarMap['NTF_CAB_VERSION_HIGH']}"
		plcVersionMVarName="#{plcVarMap['NTF_CAB_VERSION_MEDIUM']}"
		plcVersionLVarName="#{plcVarMap['NTF_CAB_VERSION_LOW']}"
	}
}




