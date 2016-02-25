import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping.*
import com.sicpa.standard.sasscl.devices.remote.impl.statusmapping.*
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.*
import com.sicpa.standard.client.common.timeout.TimeoutAspect
beans{



	xmlns context:"http://www.springframework.org/schema/context"
	context.'annotation-config'()

	xmlns aop:"http://www.springframework.org/schema/aop"
	aop.'aspectj-autoproxy'()


	stdRemoteModel(ConfigUtils,profilePath+'/config/server/remoteServer.xml'){ b->
		b.factoryMethod='load'
	}

	remoteServer(RemoteServer,ref('stdRemoteModel')){b->
		b.initMethod='init'
		productStatusMapping = ref('remoteServerProductStatusMapping')
		sicpadataPassword = props['sicpadataPassword']
		cryptoFieldsConfig = ref('cryptoFieldsConfig')
		productionModeMapping = ref('productionModeMapping')
		cryptoServiceProviderManager = ref('cryptoProviderManager')
		sdGenReceiver = ref('sicpaDataGeneratorRequestor')
		storage = ref('storage')
		lifeChecker = ref('remoteLifeChecker')
		serverPropertiesFile = profilePath+'/config/server/standard-server.properties'
	}
	remoteLifeChecker(RemoteServerLifeChecker){b->
		target = ref('remoteServer')
		lifeCheckSleep ="#{stdRemoteModel.lifeCheckSleep}"
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

