import com.sicpa.standard.sasscl.*
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}

	bootstrap(CoreBootstrap)
}