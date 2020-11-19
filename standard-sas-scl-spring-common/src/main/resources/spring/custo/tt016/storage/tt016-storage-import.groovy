beans{
	
	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()
	
	importBeans('spring/custo/tt016/storage/tt016-storage-common.xml')
	
	if(serverBehavior == "SIMULATOR") {
		importBeans('spring/custo/tt016/storage/tt016-storage-simulator.xml')
	}
	else if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/storage/tt016-storage-standard.xml')
	}
}