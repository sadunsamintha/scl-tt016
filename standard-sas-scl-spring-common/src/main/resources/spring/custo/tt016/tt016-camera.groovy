import com.sicpa.tt016.scl.camera.TT016CameraJobParametersProvider

beans{

	cameraJobParametersProvider(TT016CameraJobParametersProvider){ 
		productionParameters=ref('productionParameters')
	}
}