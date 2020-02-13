import com.sicpa.standard.sasscl.*
import com.sicpa.tt021.scl.TT021Bootstrap

beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT021Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
	
	if(serverBehavior == "STANDARD") {
		//importBeans('spring/server/server-core5.groovy')
		importBeans('spring/custo/tt021/tt021-server.groovy')
	}
}