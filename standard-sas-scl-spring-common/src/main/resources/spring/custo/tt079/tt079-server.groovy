import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.standard.sasscl.devices.remote.impl.*

beans{

	dtoConverter(TT079DtoConverter){
		productStatusMapping=ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
	}

}