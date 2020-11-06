import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.*
import com.sicpa.standard.client.common.timeout.TimeoutAspect
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.*
import com.sicpa.standard.sasscl.devices.remote.lifecheck.*
import com.sicpa.tt065.remote.impl.*

beans{

	xmlns context:"http://www.springframework.org/schema/context"
	context.'annotation-config'()

	xmlns aop:"http://www.springframework.org/schema/aop"
	aop.'aspectj-autoproxy'()


	remoteServer(TT065RemoteServer){
		cryptoServiceProviderManager = ref('cryptoProviderManager')
		sdGenReceiver = ref('sicpaDataGeneratorRequestor')
		storage = ref('storage')
		converter=ref('dtoConverter')
		remoteServices=ref('remoteServices')
		connector=ref('masterConnector')
		packageSender=ref('packageSender')
	}

	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifecheckIntervalSec =props['server.lifecheck.delay.sec'].trim()
	}
	masterConnector(MasterConnector){
		remoteServices=ref('remoteServices')
		lifeCheckWorker=ref('masterLifeCheckWorker')
	}

	sicpaDataGeneratorStorage(SicpaDataGeneratorStorage){
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		storage=ref('storage')
		subsystemIdProvider=ref('subsystemIdProvider')
		fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
	}

	sicpaDataGeneratorRequestor(SicpaDataGeneratorRequestor){ incomingStorageProvider=ref('sicpaDataGeneratorStorage') }

	timeoutAspect(TimeoutAspect){
		timeoutSec =props['remoteServerTimeoutCall_sec'].trim()
		timeoutSecLifeCheck= props['remoteServerLifeCheckTimeoutCall_sec'].trim()
	}
}

