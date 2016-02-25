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
//	




//<property name="cryptoFieldsConfig" ref="cryptoFieldsConfig" />
//<property name="storage" ref="storage" />
//<property name="subsystemIdProvider" ref="subsystemIdProvider" />
//<property name="fileSequenceStorageProvider" ref="fileSequenceStorageProvider" />
//</bean>


//	#encoder requestor for std dms

//	<bean id="sicpaDataGeneratorRequestor" class="${sicpaDataGeneratorRequestor}">
//		<property name="incomingStorageProvider" ref="sicpaDataGeneratorStorage" />
//	</bean>
//	}


