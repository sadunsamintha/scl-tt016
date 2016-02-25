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
	}

	plcStateListener(PlcStateListener){ plcProvider=ref('plcProvider') }

	plcRegisterHandler(PlcRegisterHandler) { plcProvider=ref('plcProvider') }


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
	}
}




