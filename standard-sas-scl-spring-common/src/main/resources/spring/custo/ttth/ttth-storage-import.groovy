beans{
	
	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()
	
	importBeans('spring/custo/ttth/ttth-storage-common.xml')
	
	if(serverBehavior == "SIMULATOR") {
		importBeans('spring/custo/ttth/ttth-storage-simulator.xml')
	}
	else if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/ttth/ttth-storage-standard.xml')
	}
}