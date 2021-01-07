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
		cryptoMode = props['server.simulator.cryptoMode'].trim()
		cryptoModelPreset = props['server.simulator.cryptoModelPreset'].trim()
		dailyBatchRequestRepository = ref('dailyBatchRequestRepository')
	}

}