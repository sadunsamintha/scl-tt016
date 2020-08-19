import com.sicpa.ttth.remote.simulator.TTTHRemoteServerSimulator

beans{

	remoteServer(TTTHRemoteServerSimulator, ref('simulatorRemoteModel')) {
		simulatorGui = ref('simulatorGui')
		cryptoFieldsConfig = ref('cryptoFieldsConfig')
		productionParameters = ref('productionParameters')
		serviceProviderManager = ref('cryptoProviderManager')
		storage = ref('storage')
		fileSequenceStorageProvider = ref('fileSequenceStorageProvider')
		remoteServerSimulatorOutputFolder = profilePath + '/simulProductSend'
		cryptoMode = props['server.simulator.cryptoMode']
		cryptoModelPreset = props['server.simulator.cryptoModelPreset']
		lineID = props['subsystemId']
	}

}