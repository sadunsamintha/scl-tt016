import com.sicpa.standard.sasscl.utils.ConfigUtilEx
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator
beans{

	cameraModelFile(ConfigUtilEx,profilePath+'/config/camera/simulator/cameraSimulator_####.xml',ref('deviceModelNamePostfixProperty')){ b->
		b.factoryMethod='load'
		b.scope='prototype'
	}


	cameraSimulatorController(CameraSimulatorController,ref('cameraModelFile')){ b->
		b.scope='prototype'
		productionParameters= ref('productionParameters')
		simulatorGui=ref('simulatorGui')
		aliasROI_StartRow="nStartRow"
		aliasROI_x='nDMXROIcol'
		aliasROI_y='nDMXROIrow'
		aliasROI_w='nDMXROIwide'
		aliasROI_h='nDMXROIhigh'
	}

	camera(CameraAdaptorSimulator,ref('cameraSimulatorController'),ref('cameraRoiImageTransformer')){ b->
		b.scope='prototype'
		productionParameters=ref('productionParameters')
		cameraJobFilesConfig=ref('cameraJobFilesConfig')
	}
}
