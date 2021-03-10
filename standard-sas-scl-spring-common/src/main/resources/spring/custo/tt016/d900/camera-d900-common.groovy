package custo.tt016.d900

import com.sicpa.standard.sasscl.devices.camera.d900.sku.D900CompliantProductSkuResolver
import com.sicpa.standard.sasscl.devices.camera.d900.sku.D900CompliantProduct
import com.sicpa.standard.sasscl.devices.camera.d900.D900CodeCheck

beans{
	
	//d900CameraJobParametersProvider(NoCameraJobParametersProvider)
    d900CompliantProduct(D900CompliantProductSkuResolver)

    d900CodeCheck(D900CodeCheck){
        d900CompliantProductResolver=ref('d900CompliantProduct')
    }
}
