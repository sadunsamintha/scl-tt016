import com.sicpa.standard.sasscl.devices.remote.lifecheck.MasterLifeCheckWorker
import com.sicpa.tt016.scl.remote.TT016RemoteServer
import com.sicpa.tt016.scl.remote.remoteservices.TT016RemoteServices
import com.sicpa.tt016.scl.remote.TT016MasterConnector
import com.sicpa.tt016.scl.remote.assembler.SkuConverter


beans{

	remoteServer(TT016RemoteServer){
		remoteServices=ref('remoteServices')
		connector=ref('masterConnector')
		skuConverter=ref('skuConverter')
		storage=ref('storage')
	}

	remoteServices(TT016RemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		subsystemId=props['server.machine.subsystemId']
		url=props['server.url']
		withBis=true // we can assume that for all mscl the bis services will be activated
	}
		
	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifecheckIntervalSec =props['server.lifecheck.delay.sec']
	}
	masterConnector(TT016MasterConnector){
		remoteServices=ref('remoteServices')
		lifeCheckWorker=ref('masterLifeCheckWorker')
	}
	
	skuConverter(SkuConverter){
		codeTypeId=props['codeTypeId']
		refeedAvailable=props['refeedAvailable']
		
//		productionNormal = props['productionmode.standard']
//		productionRefeedNormal = props['productionmode.refeed.normal']
//		productionRefeedCorr =props['productionmode.refeed.correction']
		//refeedSkuProvider=ref('refeedSkuProvider')
	}
}