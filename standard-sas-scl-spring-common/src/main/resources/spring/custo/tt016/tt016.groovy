import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert

beans{

	tt016TrilightWarningCameraAlert(TT016TrilightWarningCameraAlert) {
		plcParamSender=ref('plcParamSender')
		reqJavaErrorRegisterVar= ref('REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER_var')
		plcProvider=ref('plcProvider')
	}

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
		codeTypeId=props['codeTypeId']
		unknownSkuProvider=ref('unknownSkuProvider')
	}

	importBeans('spring/custo/tt016/tt016View.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

}