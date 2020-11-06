import com.sicpa.standard.sasscl.*
import com.sicpa.tt053.scl.TT053Bootstrap
import com.sicpa.tt053.scl.business.activation.TT053ExportActivationBehavior

beans{

	def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()
	
	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}
	
	if(serverBehavior == "SIMULATOR") {
		importBeans('spring/custo/tt053/tt053-server-simulator.groovy')
	}
	
	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT053Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
	
	addAlias('exportActivationBehaviorAlias','exportActivationBehavior')
	exportActivationBehavior(TT053ExportActivationBehavior){b->
		b.parent=ref('exportActivationBehaviorAlias')
	}
	
	importBeans('spring/custo/tt053/tt053-postPackage.xml')
	importBeans('spring/custo/tt053/tt053-coding.xml')
	importBeans('spring/custo/tt053/tt053-printer-common.groovy')
	
}