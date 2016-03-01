import com.sicpa.standard.sasscl.devices.remote.stdCrypto.CryptoFieldsConfig
import com.sicpa.standard.client.common.ioc.InjectByMethodBean

beans{
	cryptoFieldsConfig(CryptoFieldsConfig)

	startupGroupAddRemote(InjectByMethodBean) {
		target=ref('startupDevicesGroup')
		methodName ='addDevice'
		params=ref('remoteServer')
	}
}

