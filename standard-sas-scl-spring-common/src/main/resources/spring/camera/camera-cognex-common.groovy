import com.sicpa.standard.sasscl.devices.camera.transformer.CameraImageRotator
import com.sicpa.standard.sasscl.devices.camera.transformer.CropImageTransformer
import com.sicpa.standard.sasscl.devices.camera.transformer.OneAreaRoiImageTransformer

beans{

	cameraImageRotator(CameraImageRotator){b->
		b.scope='prototype'
	}
	cropImageTransformer(CropImageTransformer){b->
		b.scope='prototype'
	}
	cameraRoiImageTransformer(OneAreaRoiImageTransformer){b->
		b.scope='prototype'
		aliasROI_StartRow="nStartRow"
		aliasROI_x='nDMXROIcol'
		aliasROI_y='nDMXROIrow'
		aliasROI_w='nDMXROIwide'
		aliasROI_h='nDMXROIhigh'
	}
}