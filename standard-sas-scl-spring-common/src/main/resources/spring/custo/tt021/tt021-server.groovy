import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.client.common.timeout.TimeoutAspect
import com.sicpa.standard.sasscl.devices.remote.lifecheck.*
import com.sicpa.standard.sasscl.devices.remote.services.TT021RemoteServer
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.*
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.standard.sasscl.devices.remote.impl.TT021MasterConnector
import com.sicpa.standard.sasscl.devices.remote.impl.TT021PackageSenderGlobal
import com.sicpa.standard.sasscl.devices.remote.impl.DefaultRemoteServerProductStatusMapping
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.TT021RemoteServices
import com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping.TT021DefaultProductionModeMapping
import com.sicpa.standard.sasscl.business.production.impl.TT021Production
beans{

	xmlns context:"http://www.springframework.org/schema/context"
	context.'annotation-config'()

	xmlns aop:"http://www.springframework.org/schema/aop"
	aop.'aspectj-autoproxy'()
	
	remoteServer(TT021RemoteServer){
		remoteServices=ref('remoteServices')
		cryptoServiceProviderManager = ref('cryptoProviderManager')
		connector=ref('masterConnector')
		converter=ref('dtoConverter')
		storage=ref('storage')
		sdGenReceiver = ref('sicpaDataGeneratorRequestor')
		packageSender=ref('packageSender')
	}
	
	remoteServices(TT021RemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		properties=ref('allProperties')
	}
	
	dtoConverter(DtoConverter){
		productStatusMapping= ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
	}
	
	skuConverter(SkuConverter){ 
		productionModeMapping=ref('productionModeMapping') 
	}
	
	packageSender(TT021PackageSenderGlobal){
		converter=ref('dtoConverter')
		remoteServices=ref('remoteServices')
	}
	
	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifecheckIntervalSec =props['server.lifecheck.delay.sec']
	}
	masterConnector(TT021MasterConnector){
		remoteServices=ref('remoteServices')
		lifeCheckWorker=ref('masterLifeCheckWorker')
	}
	
	productionModeMapping(TT021DefaultProductionModeMapping, 0,1,2,5)
	
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
	
		production(TT021Production){
		storage=ref('storage')
		remoteServer=ref('remoteServer')
		productsPackager=ref('productsPackager')
		subsystemIdProvider=ref('subsystemIdProvider')
		productionSendBatchSize= props['productionSendBatchSize']
		productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold']
	}
}