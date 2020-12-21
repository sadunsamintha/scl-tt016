import com.sicpa.tt085.scl.TT085Bootstrap

beans{

	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT085Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
	
	importBeans('spring/custo/tt085/tt085-view.xml')
}