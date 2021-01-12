import com.sicpa.tt085.remote.remoteservices.TT085RemoteServices;
import com.sicpa.tt085.devices.remote.impl.TT085PackageSenderGlobal;
import com.sicpa.tt085.devices.remote.impl.TT085DefaultProductionModeMapping;
import com.sicpa.tt085.business.production.impl.TT085Production;

beans{

	remoteServices(TT085RemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		properties=ref('allProperties')
	}
	
	packageSender(TT085PackageSenderGlobal){
		remoteServices=ref('remoteServices')
	}
	
	productionModeMapping(TT085DefaultProductionModeMapping)
	
	production(TT085Production){
		storage=ref('storage')
		remoteServer=ref('remoteServer')
		productsPackager=ref('productsPackager')
		subsystemIdProvider=ref('subsystemIdProvider')
		productionSendBatchSize= props['productionSendBatchSize']
		productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold']
		pp=ref('productionParameters')
	}
	
}
