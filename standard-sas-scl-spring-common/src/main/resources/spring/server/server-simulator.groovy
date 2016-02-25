import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator
beans{

	simulatorRemoteModel(ConfigUtils,profilePath+'/config/server/simulator/remoteServerSimulator.xml'){ b->
		b.factoryMethod='load'
	}

	remoteServer(RemoteServerSimulator,ref('simulatorRemoteModel')){
		simulatorGui=ref('simulatorGui')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		productionParameters=ref('productionParameters')
		serviceProviderManager=ref('cryptoProviderManager')
		storage=ref('storage')
		fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
		remoteServerSimulatorOutputFolder = profilePath+'/simulProductSend'
	}
}

