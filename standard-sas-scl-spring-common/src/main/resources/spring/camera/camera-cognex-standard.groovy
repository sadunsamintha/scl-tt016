import com.sicpa.standard.sasscl.utils.ConfigUtilEx
import com.sicpa.standard.camera.controller.internal.CognexCameraControllerImpl
import com.sicpa.standard.sasscl.devices.camera.CameraAdaptor
beans{

	cameraModelFile(ConfigUtilEx,profilePath+'/config/camera/camera_####.xml',ref('deviceModelNamePostfixProperty')){	b->
		b.factoryMethod='load'
		b.scope='prototype'
	}

	stdCameraController(CognexCameraControllerImpl,ref('cameraModelFile')){b->
		b.scope='prototype'
	}

	camera(CameraAdaptor,ref('stdCameraController'),ref('cameraRoiImageTransformer')){b->
		b.scope='prototype'
		productionParameters=ref('productionParameters')
		cameraJobFilesConfig=ref('cameraJobFilesConfig')
		cameraImageTransformers = [
			ref('cropImageTransformer'),
			ref('cameraImageRotator')
		]
		cameraJobParametersProvider=ref('cameraJobParametersProvider')
	}
}