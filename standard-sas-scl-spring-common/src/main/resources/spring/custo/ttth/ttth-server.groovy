import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*

beans{

	dtoConverter(TTTHDtoConverter){
		productStatusMapping=ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
	}

	skuConverter(TTTHSkuConverter) {
		productionModeMapping=ref('productionModeMapping')
	}

}