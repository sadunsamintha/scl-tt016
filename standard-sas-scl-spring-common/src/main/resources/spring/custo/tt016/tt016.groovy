import com.sicpa.tt016.scl.remote.simulator.TT016RemoteServerSimulator
import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider
import com.sicpa.tt016.business.ejection.EjectionTypeSender

beans{
	tt016TrilightWarningCameraAlert(TT016TrilightWarningCameraAlert) {
		plcParamSender=ref('plcParamSender')
		reqJavaErrorRegisterVar= ref('REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER_var')
		plcProvider=ref('plcProvider')
	}

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	} else {
		remoteServer(TT016RemoteServerSimulator,ref('simulatorRemoteModel')){
			simulatorGui=ref('simulatorGui')
			cryptoFieldsConfig=ref('cryptoFieldsConfig')
			productionParameters=ref('productionParameters')
			serviceProviderManager=ref('cryptoProviderManager')
			storage=ref('storage')
			fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
			remoteServerSimulatorOutputFolder = profilePath+'/simulProductSend'
			cryptoMode=props['server.simulator.cryptoMode']
			cryptoModelPreset=props['server.simulator.cryptoModelPreset']
			isRefeedAvailable=props['refeedAvailable']
		}
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
		codeTypeId=props['codeTypeId']
		unknownSkuProvider=ref('unknownSkuProvider')
		remoteServerRefeedAvailability=ref('remoteServer')
		refeedAvailabilityProvider=ref('refeedAvailabilityProvider')
		ejectionTypeSender=ref('ejectionTypeSender')
	}

	importBeans('spring/custo/tt016/tt016-plc.xml')
	importBeans('spring/custo/tt016/tt016-activation.xml')
	importBeans('spring/custo/tt016/tt016-view.xml')
	importBeans('spring/custo/tt016/tt016-postPackage.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

	importBeans('spring/custo/tt016/tt016-camera.groovy')


	refeedAvailabilityProvider(TT016RefeedAvailabilityProvider){
		isRefeedAvailableInRemoteServer=props['refeedAvailable']
		isHeuftSystem=props['heuftSystem']
	}


    //values may or may not be present but all available production modes plus default are specified for full config support
	def ejectionTypeProductionModeOverride = [:]
	ejectionTypeProductionModeOverride.put("productionmode.DEFAULT",props['ejection.type.default'])
	ejectionTypeProductionModeOverride.put("productionmode.standard",props['ejection.type.productionmode.standard'])
	ejectionTypeProductionModeOverride.put("productionmode.export",props['ejection.type.productionmode.export'])
	ejectionTypeProductionModeOverride.put("productionmode.refeed.normal",props['ejection.type.productionmode.refeed.normal'])
	ejectionTypeProductionModeOverride.put("productionmode.maintenance",props['ejection.type.productionmode.maintenance'])


	ejectionTypeSender(EjectionTypeSender){
		plcMap= ref('plcVarMap')
		plcParamSender= plcParamSender
		overrideParameters=ejectionTypeProductionModeOverride

	}
}