import com.sicpa.tt016.scl.TT016Bootstrap

beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
	}

	importBeans('spring/custo/tt016/tt016View.xml')
	importBeans('spring/offlineCounting.xml')
	
	addAlias('bisCredentialProvider','remoteServer')
}