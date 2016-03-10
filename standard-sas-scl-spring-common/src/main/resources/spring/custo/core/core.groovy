import com.sicpa.standard.sasscl.*
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(CoreBootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
}