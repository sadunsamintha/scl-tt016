import com.sicpa.tt085.scl.TT085Bootstrap
import com.sicpa.tt085.scl.business.activation.TT085ExportActivationBehavior

beans{

	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
		importBeans('spring/custo/tt085/tt085-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT085Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
	
	addAlias('exportActivationBehaviorAlias','exportActivationBehavior')
	exportActivationBehavior(TT085ExportActivationBehavior){b->
		b.parent=ref('exportActivationBehaviorAlias')
	}
	
	importBeans('spring/custo/tt085/tt085-view.xml')
}