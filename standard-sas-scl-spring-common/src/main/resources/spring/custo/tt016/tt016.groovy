import com.sicpa.tt016.business.ejection.EjectionTypeSender
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert
import com.sicpa.tt016.model.DisallowedConfiguration
import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.util.LegacyEncoderConverter
import com.sicpa.tt016.printer.simulator.TT016PrinterAdaptorSimulator
import com.sicpa.tt016.devices.plc.impl.TT016PlcLoader

beans{
	tt016TrilightWarningCameraAlert(TT016TrilightWarningCameraAlert) {
		plcParamSender=ref('plcParamSender')
		reqJavaErrorRegisterVar= ref('REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER_var')
		plcProvider=ref('plcProvider')
	}

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()
	def printerBehavior=props['printer.behavior'].toUpperCase()
	def productionBehavior=props['production.config.folder'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	} else {
		importBeans('spring/custo/tt016/tt016-server-simulator.groovy')
	}
	
	importBeans('spring/custo/tt016/tt016-hrd.xml')

    printerSimulatorAdaptor(TT016PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
        b.scope='prototype'
    }

    if(printerBehavior == 'SIMULATOR') {
        addAlias('printerLeibinger','printerSimulatorAdaptor')
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
		ejectionTypeSender=ref('ejectionTypeSender')
		automatedBeamHeightManager=ref('automatedBeamHeightManager')
		automatedBeamNtfHandler=ref('automatedBeamNtfHandler')
		disallowedConfigurations=disallowedConf
		defaultLang=props['language']
		legacyEncoderConverter=ref('legacyEncoderConverter')
		productionBehaviorVar=props['production.config.folder'].toUpperCase()
		if (productionBehaviorVar == "PRODUCTIONCONFIG-SAS") {
			sasAppLegacyMBean=ref('legacyStatsMBean')
		} else {
			sclAppLegacyMBean=ref('legacyStatsMBean')
		}
	}

	addAlias('plcValuesLoaderAlias','plcValuesLoader')
	plcValuesLoader(TT016PlcLoader){b->
		b.parent=ref('plcValuesLoaderAlias')
		wiperEnabled=props['wiper.enabled']
		automatedBeamEnabled=props['automated.beam.enabled']
	}

	importBeans('spring/custo/tt016/tt016-plc.xml')
	
	if (productionBehavior == "PRODUCTIONCONFIG-SAS") {
		importBeans('spring/custo/tt016/tt016-activation-sas.xml')
		importBeans('spring/custo/tt016/tt016-monitoring-sas.xml')
	} else {
		importBeans('spring/custo/tt016/tt016-activation.xml')
		importBeans('spring/custo/tt016/tt016-monitoring-scl.xml')
	}
	
	importBeans('spring/custo/tt016/tt016-view.xml')
	importBeans('spring/custo/tt016/tt016-postPackage.xml')
	importBeans('spring/custo/tt016/tt016-coding.xml')
	importBeans('spring/offlineCounting.xml')
	importBeans('spring/custo/tt016/tt016-provider.xml')
	importBeans('spring/custo/tt016/tt016-scheduler.xml')
	importBeans('spring/custo/tt016/tt016-alertPlcActivationCrossCheckTask.xml')

	addAlias('bisCredentialProvider','remoteServer')

	importBeans('spring/custo/tt016/tt016-camera.groovy')

    //values may or may not be present but all available production modes plus default are specified for full config support
	def ejectionTypeProductionModeOverride = [:]
	ejectionTypeProductionModeOverride.put("productionmode.DEFAULT",props['ejection.type.default'])
	ejectionTypeProductionModeOverride.put("productionmode.standard",props['ejection.type.productionmode.standard'])
	ejectionTypeProductionModeOverride.put("productionmode.export",props['ejection.type.productionmode.export'])
	ejectionTypeProductionModeOverride.put("productionmode.refeed.normal",props['ejection.type.productionmode.refeed.normal'])
	ejectionTypeProductionModeOverride.put("productionmode.maintenance",props['ejection.type.productionmode.maintenance'])


	ejectionTypeSender(EjectionTypeSender){
		plcParamSender=plcParamSender
		overrideParameters=ejectionTypeProductionModeOverride
	}

	legacyEncoderConverter(LegacyEncoderConverter) {
		fileStorage=ref('storage')
		codeTypeId=props['codeTypeId']
	}
}