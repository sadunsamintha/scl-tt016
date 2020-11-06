import com.sicpa.standard.sasscl.devices.remote.lifecheck.MasterLifeCheckWorker
import com.sicpa.tt016.remote.mapping.TT016ProductionModeMapping
import com.sicpa.tt016.scl.remote.TT016RemoteServer
import com.sicpa.tt016.scl.remote.TT016RemoteServerSAS
import com.sicpa.tt016.scl.remote.remoteservices.TT016RemoteServices
import com.sicpa.tt016.scl.remote.remoteservices.TT016RemoteServicesMSASLegacy
import com.sicpa.tt016.scl.remote.TT016MasterConnector
import com.sicpa.tt016.scl.remote.TT016MasterConnectorSAS
import com.sicpa.tt016.scl.remote.assembler.SkuConverter


beans{
	
	def msasLegacy = props['production.config.server.msaslegacy'].trim().toUpperCase()

	if(msasLegacy == 'TRUE'){
		
		remoteServices(TT016RemoteServicesMSASLegacy){
			userMachine=props['server.machine.user'].trim()
			passwordMachine=props['server.machine.password'].trim()
			subsystemId=props['server.machine.subsystemId'].trim()
			url=props['server.url'].trim()
			decoderPassword=props['decoderPassword'].trim()

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
			userMachine=props['server.machine.user'].trim()
			passwordMachine=props['server.machine.password'].trim()
			subsystemId=props['server.machine.subsystemId'].trim()
			url=props['server.url'].trim()
			withBis=true // we can assume that for all mscl the bis services will be activated
		}
		
		remoteServer(TT016RemoteServer){
			remoteServices=ref('remoteServices')
			connector=ref('masterConnector')
			skuConverter=ref('skuConverter')
			storage=ref('storage')
			fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
		}

		masterConnector(TT016MasterConnector){
			remoteServices=ref('remoteServices')
			lifeCheckWorker=ref('masterLifeCheckWorker')
		}
		
	}
	
		


	masterLifeCheckWorker(MasterLifeCheckWorker){b->
		connector = ref('masterConnector')
		lifecheckIntervalSec =props['server.lifecheck.delay.sec'].trim()
	}
	
	skuConverter(SkuConverter){
		codeTypeId=props['codeTypeId'].trim()
		refeedAvailable=props['refeedAvailable'].trim()
		heightAvailable=props['automated.beam.enabled'].trim()
	}
	
	productionModeMapping(TT016ProductionModeMapping)
}