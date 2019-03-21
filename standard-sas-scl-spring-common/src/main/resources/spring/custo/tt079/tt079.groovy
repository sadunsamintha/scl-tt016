import com.sicpa.standard.sasscl.*
import com.sicpa.tt079.scl.TT079Bootstrap
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT079Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		productionBatchProvider=ref('productionBatchProvider')
	}
	
	importBeans('spring/custo/tt079/tt079-view.groovy')
    importBeans('spring/custo/tt079/tt079-provider.groovy')
    importBeans('spring/custo/tt079/tt079-flowControl.groovy')
}