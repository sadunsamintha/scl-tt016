import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.*
import com.sicpa.standard.client.common.timeout.TimeoutAspect
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.*
import com.sicpa.standard.sasscl.devices.remote.lifecheck.*

beans{

	xmlns context:"http://www.springframework.org/schema/context"
	context.'annotation-config'()

	xmlns aop:"http://www.springframework.org/schema/aop"
	aop.'aspectj-autoproxy'()


	remoteServer(RemoteServer){
		cryptoServiceProviderManager = ref('cryptoProviderManager')
		sdGenReceiver = ref('sicpaDataGeneratorRequestor')
		storage = ref('storage')
		converter=ref('dtoConverter')
		remoteServices=ref('remoteServices')
		connector=ref('masterConnector')
		packageSender=ref('packageSender')
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
	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifechecIntervalSec =props['server.lifecheck.delay.sec']
	}
	masterConnector(MasterConnector){
		remoteServices=ref('remoteServices')
		lifeCheckWorker=ref('masterLifeCheckWorker')
	}

	productionModeMapping(DefaultProductionModeMapping)
	remoteServerProductStatusMapping(DefaultRemoteServerProductStatusMapping)


	sicpaDataGeneratorStorage(SicpaDataGeneratorStorage){
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		storage=ref('storage')
		subsystemIdProvider=ref('subsystemIdProvider')
		fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
	}

	sicpaDataGeneratorRequestor(SicpaDataGeneratorRequestor){ incomingStorageProvider=ref('sicpaDataGeneratorStorage') }

	timeoutAspect(TimeoutAspect){
		timeoutSec =props['remoteServerTimeoutCall_sec']
		timeoutSecLifeCheck= props['remoteServerLifeCheckTimeoutCall_sec']
	}
}

