import com.sicpa.tt017.scl.TT017Bootstrap
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT017Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
}