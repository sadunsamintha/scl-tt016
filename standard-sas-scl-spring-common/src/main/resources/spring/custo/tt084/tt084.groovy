import com.sicpa.standard.sasscl.*
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
		importBeans('spring/custo/tt084/tt084-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT084Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}

	importBeans('spring/custo/tt084/tt084-production-config.groovy')
}