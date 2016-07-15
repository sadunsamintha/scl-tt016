import com.sicpa.tt016.scl.camera.TT016CameraJobParametersProvider
import com.sicpa.tt016.devices.camera.TT016CameraSimulatorController

beans{
	def plcBehavior=props['plc.behavior'].toUpperCase()
	def cameraBehavior=props['camera.behavior'].toUpperCase()

	if (plcBehavior == "SIMULATOR" && cameraBehavior == "SIMULATOR") {
		addAlias('cameraSimulatorControllerAlias', 'cameraSimulatorController')
		cameraSimulatorController(TT016CameraSimulatorController){ b->
			b.parent=ref('cameraSimulatorControllerAlias')
		}
	}

	cameraJobParametersProvider(TT016CameraJobParametersProvider){ 
		productionParameters=ref('productionParameters')
	}
}