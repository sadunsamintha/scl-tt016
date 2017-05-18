import com.sicpa.standard.sasscl.devices.remote.stdCrypto.CryptoFieldsConfig
import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.*
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*

beans{
	cryptoFieldsConfig(CryptoFieldsConfig)

	startupGroupAddRemote(InjectByMethodBean) {
		target=ref('startupDevicesGroup')
		methodName ='addDevice'
		params=ref('remoteServer')
	}

	remoteServices(RemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		properties=ref('allProperties')
	}

	dtoConverter(DtoConverter){
		productStatusMapping= ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
	}

	skuConverter(SkuConverter){ productionModeMapping=ref('productionModeMapping') }

	packageSender(PackageSenderGlobal){
		converter=ref('dtoConverter')
		remoteServices=ref('remoteServices')
	}

	productionModeMapping(DefaultProductionModeMapping)
	remoteServerProductStatusMapping(DefaultRemoteServerProductStatusMapping)
}

