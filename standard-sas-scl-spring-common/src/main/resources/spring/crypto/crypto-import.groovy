beans {

	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()

	importBeans('spring/crypto/crypto-common.xml')

	if(serverBehavior == "SIMULATOR") {
		importBeans('spring/crypto/crypto-simulator.xml')
	}
	else if(serverBehavior == "STANDARD") {
		importBeans('spring/crypto/crypto-standard.xml')
	}
}