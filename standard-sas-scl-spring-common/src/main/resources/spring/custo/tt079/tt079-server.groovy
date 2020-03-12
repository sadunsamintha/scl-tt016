import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.tt079.remote.mapping.TT079ProductionModeMapping

beans{

	dtoConverter(TT079DtoConverter){
		productStatusMapping=ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
	}

	remoteServerProductStatusMapping(DefaultRemoteServerProductStatusMapping)

	skuConverter(SkuConverter){
		productionModeMapping=ref('productionModeMapping')
	}

	productionModeMapping(TT079ProductionModeMapping)

}