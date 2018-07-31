import com.sicpa.standard.sasscl.devices.remote.lifecheck.MasterLifeCheckWorker
import com.sicpa.tt016.scl.remote.TT016RemoteServer
import com.sicpa.tt016.scl.remote.TT016RemoteServerSAS
import com.sicpa.tt016.scl.remote.remoteservices.TT016RemoteServices
import com.sicpa.tt016.scl.remote.remoteservices.TT016RemoteServicesMSASLegacy
import com.sicpa.tt016.scl.remote.TT016MasterConnector
import com.sicpa.tt016.scl.remote.TT016MasterConnectorSAS
import com.sicpa.tt016.scl.remote.assembler.SkuConverter


beans{

	if(props['production.config.server.msaslegacy']){
		
		remoteServices(TT016RemoteServicesMSASLegacy){
			userMachine=props['server.machine.user']
			passwordMachine=props['server.machine.password']
			subsystemId=props['server.machine.subsystemId']
			url=props['server.url']
		}
		
		remoteServer(TT016RemoteServerSAS){
			remoteServices=ref('remoteServices')
			connector=ref('masterConnector')
			skuConverter=ref('skuConverter')
			storage=ref('storage')
		}
		
		masterConnector(TT016MasterConnectorSAS){
			remoteServices=ref('remoteServices')
			lifeCheckWorker=ref('masterLifeCheckWorker')
		}
		
	} else {
	
		remoteServices(TT016RemoteServices){
			userMachine=props['server.machine.user']
			passwordMachine=props['server.machine.password']
			subsystemId=props['server.machine.subsystemId']
			url=props['server.url']
			withBis=true // we can assume that for all mscl the bis services will be activated
		}
		
		remoteServer(TT016RemoteServer){
			remoteServices=ref('remoteServices')
			connector=ref('masterConnector')
			skuConverter=ref('skuConverter')
			storage=ref('storage')
		}

		masterConnector(TT016MasterConnector){
			remoteServices=ref('remoteServices')
			lifeCheckWorker=ref('masterLifeCheckWorker')
		}
		
	}
	
		


	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifecheckIntervalSec =props['server.lifecheck.delay.sec']
	}
	
	skuConverter(SkuConverter){
		codeTypeId=props['codeTypeId']
		refeedAvailable=props['refeedAvailable']
		
	}
}