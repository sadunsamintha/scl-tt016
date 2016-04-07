import com.sicpa.standard.sasscl.devices.remote.lifecheck.MasterLifeCheckWorker;
import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.scl.remote.remoteservices.*
import com.sicpa.tt016.scl.remote.*
import com.sicpa.tt016.scl.remote.*
import com.sicpa.tt016.scl.remote.assembler.*


beans{

	remoteServer(TT016RemoteServer){
		remoteServices=ref('remoteServices')
		connector=ref('masterConnector')
		skuConverter=ref('skuConverter')
	}
	remoteServices(TT016RemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		subsystemId=props['server.machine.subsystemId']
		url=props['server.url']
	}
		
	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifechecIntervalSec =props['server.lifecheck.delay.sec']
	}
	masterConnector(TT016MasterConnector){
		remoteServices=ref('remoteServices')
		lifeCheckWorker=ref('masterLifeCheckWorker')
	}
	
	SkuConverter(SkuConverter){
		codeTypeId=props['codeTypeId']
	}
}