import com.sicpa.tt016.controller.flow.ProductStatusMerger
import com.sicpa.tt016.devices.camera.TT016CameraSimulatorController
import com.sicpa.tt016.scl.TT016Bootstrap

beans{
	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
	}

	productStatusMerger(ProductStatusMerger) {
		plcCameraResultIndexManager=ref('plcCameraResultIndexManager')
	}

	addAlias('cameraSimulatorControllerAlias', 'cameraSimulatorController')
	cameraSimulatorController(TT016CameraSimulatorController){ b->
		b.parent=ref('cameraSimulatorControllerAlias')
	}

	importBeans('spring/custo/tt016/tt016Plc.xml')
	importBeans('spring/custo/tt016/tt016View.xml')
	importBeans('spring/custo/tt016/tt016Activation.xml')
	importBeans('spring/custo/tt016/tt016Statistics.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

}