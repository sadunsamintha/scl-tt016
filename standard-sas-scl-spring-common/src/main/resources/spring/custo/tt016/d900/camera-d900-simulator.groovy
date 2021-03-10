package custo.tt016.d900

import com.sicpa.standard.sasscl.utils.ConfigUtilEx
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator
beans{

	d900CameraModelFile(ConfigUtilEx,profilePath+'/config/d900/simulator/d900Simulator_####.xml',ref('deviceModelNamePostfixProperty')){ b->
		b.factoryMethod='load'
		b.scope='prototype'
	}


	d900CameraSimulatorController(D900CameraSimulatorController,ref('d900CameraModelFile')){ b->
		b.scope='prototype'
		productionParameters= ref('productionParameters')
		simulatorGui=ref('simulatorGui')
		aliasROI_StartRow="nStartRow"
		/*aliasROI_x='nDMXROIcol'
		aliasROI_y='nDMXROIrow'
		aliasROI_w='nDMXROIwide'
		aliasROI_h='nDMXROIhigh'*/
	}

	d900Camera(D900CameraAdaptorSimulator,ref('d900CameraSimulatorController')){ b->
		b.scope='prototype'
		productionParameters=ref('productionParameters')
		//cameraJobFilesConfig=ref('cameraJobFilesConfig')
		//cameraJobParametersProvider=ref('cameraJobParametersProvider')
	}
}
