beans{
	
	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()
	
	importBeans('spring/custo/tt079/tt079-storage-common.xml')
	
	if(serverBehavior == "SIMULATOR") {
		importBeans('spring/custo/tt079/tt079-storage-simulator.xml')
	}
	else if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt079/tt079-storage-standard.xml')
	}
}