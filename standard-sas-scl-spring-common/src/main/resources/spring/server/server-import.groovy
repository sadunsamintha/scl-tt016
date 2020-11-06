beans {
	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()


	importBeans('spring/server/server-common.groovy')	if(serverBehavior == "SIMULATOR") {		importBeans('spring/server/server-simulator.groovy')
	}	
}

