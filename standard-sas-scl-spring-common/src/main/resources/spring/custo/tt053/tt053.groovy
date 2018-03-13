import com.sicpa.standard.sasscl.*
import com.sicpa.tt053.scl.TT053Bootstrap
beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()
	
	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
	}
	
	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT053Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
}