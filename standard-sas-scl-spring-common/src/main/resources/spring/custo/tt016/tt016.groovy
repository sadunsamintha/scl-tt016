import com.sicpa.tt016.controller.flow.ProductStatusMerger
import com.sicpa.tt016.scl.remote.simulator.TT016RemoteServerSimulator
import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider

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
	}

	productStatusMerger(ProductStatusMerger) {
		plcCameraResultIndexManager=ref('plcCameraResultIndexManager')
	}

	importBeans('spring/custo/tt016/tt016Plc.xml')
	importBeans('spring/custo/tt016/tt016Activation.xml')
	importBeans('spring/custo/tt016/tt016Statistics.xml')
	importBeans('spring/custo/tt016/tt016-view.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

	importBeans('spring/custo/tt016/tt016-camera.groovy')


	refeedAvailabilityProvider(TT016RefeedAvailabilityProvider){
		isRefeedAvailableInRemoteServer=props['refeedAvailable']
		isHeuftSystem=props['heuftSystem']
	}
}