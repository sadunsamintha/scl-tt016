package custo.tt016.d900

import com.sicpa.standard.sasscl.utils.ConfigUtilEx
import com.sicpa.standard.camera.d900.controller.internal.D900CameraControllerImpl
import com.sicpa.standard.sasscl.devices.camera.d900.D900CameraAdaptor
import com.sicpa.standard.sasscl.devices.plc.D900PlcNtfHandler

beans{

	d900CameraModelFile(ConfigUtilEx,profilePath+'/config/d900/camera_####.xml',ref('deviceModelNamePostfixProperty')){	b->
		b.factoryMethod='load'
		b.scope='prototype'
	}

	stdD900CameraController(D900CameraControllerImpl,ref('d900CameraModelFile')){b->
		b.scope='prototype'
	}

	d900Camera(D900CameraAdaptor,ref('stdD900CameraController')){b->
		b.scope='prototype'
		productionParameters=ref('productionParameters')
	}

	d900PlcNtfHandler(D900PlcNtfHandler) { b->
		plcProvider=ref('plcProvider')
	}
}
