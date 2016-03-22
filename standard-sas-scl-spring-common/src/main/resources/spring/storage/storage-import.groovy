beans {
	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	importBeans('spring/storage/storage-common.xml')	if(serverBehavior == "SIMULATOR") {		importBeans('spring/storage/storage-simulator.xml')
	}	else if(serverBehavior == "STANDARD") {		importBeans('spring/storage/storage-standard.xml')
	}
}

