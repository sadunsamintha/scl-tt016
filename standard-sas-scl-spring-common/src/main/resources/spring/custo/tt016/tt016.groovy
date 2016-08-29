import com.sicpa.tt016.scl.remote.simulator.TT016RemoteServerSimulator
import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider
import com.sicpa.tt016.business.ejection.EjectionTypeSender
import com.sicpa.tt016.model.DisallowedConfiguration




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


	//define disallowed configurations
	def dcProps1 = [:]
	dcProps1.put("useBarcodeReader","true")
	dcProps1.put("skuSelection.behavior","operator_partial")
	def dc1 = new DisallowedConfiguration(dcProps1,"If selecting SKU using a barcode reader, it implies that the sku selection is done by operator,change the sku selection behavior, or the use of barcode reader")

	def dcProps2 = [:]
	dcProps2.put("sku.recognition.behavior","check")
	dcProps2.put("skuSelection.behavior","operator_full")
	def dc2 = new DisallowedConfiguration(dcProps2,"You have specified operator is in full control of SKU selection but sku recognition as \"check\" indicating that you intend to use BRS.")

	def dcProps3 = [:]
	dcProps3.put("sku.recognition.behavior","selector")
	dcProps3.put("skuSelection.behavior","operator_full")
	def dc3 = new DisallowedConfiguration(dcProps3,"You have specified operator is in full control of SKU selection but sku recognition as \"selector\" indicating that you intend to use BIS.")


	def disallowedConf = []
	disallowedConf.add(dc1)
	disallowedConf.add(dc2)
	disallowedConf.add(dc3)

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
		disallowedConfigurations=disallowedConf
	}

	importBeans('spring/custo/tt016/tt016-plc.xml')
	importBeans('spring/custo/tt016/tt016-activation.xml')
	importBeans('spring/custo/tt016/tt016-view.xml')vi 
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