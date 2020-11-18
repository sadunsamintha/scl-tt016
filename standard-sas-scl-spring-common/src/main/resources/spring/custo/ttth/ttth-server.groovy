import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.*
import com.sicpa.ttth.remote.server.TTTHRemoteServer
import com.sicpa.ttth.scl.remote.remoteservices.TTTHRemoteServices

beans{

	dtoConverter(TTTHDtoConverter){
		productStatusMapping=ref('remoteServerProductStatusMapping')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		skuConverter=ref('skuConverter')
		dailyBatchRequestRepository=ref('dailyBatchRequestRepository')
	}

	skuConverter(TTTHSkuConverter) {
		productionModeMapping=ref('productionModeMapping')
		dailyBatchRequestRepository=ref('dailyBatchRequestRepository')
	}

	remoteServices(TTTHRemoteServices){
		userMachine=props['server.machine.user']
		passwordMachine=props['server.machine.password']
		properties=ref('allProperties')
	}

	addAlias('remoteServerAlias','remoteServer')
	remoteServer(TTTHRemoteServer){ b->
		b.parent = ref('remoteServerAlias')
		ttthRemoteServices = ref('remoteServices')
	}

}