import com.sicpa.standard.sasscl.*
import com.sicpa.tt079.scl.TT079Bootstrap

beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()
	def productionConfigFolder=props['production.config.folder'].toUpperCase()

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT079Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}
	
	importBeans('spring/custo/tt079/tt079-view.groovy')
	importBeans('spring/custo/tt079/tt079-production.groovy')
	
	if(serverBehavior == "STANDARD") {
		importBeans('spring/server/server-core5.groovy')
		importBeans('spring/custo/tt079/tt079-server.groovy')
	}
	
	importBeans('spring/custo/tt079/tt079-hrd.xml')
	
	if(productionConfigFolder == "PRODUCTIONCONFIG-SAS") {
		def externalActivation=props['external.activation'].toUpperCase()
		
		if(externalActivation == "TRUE") {
			importBeans('spring/custo/tt079/tt079-storage-import.groovy')
		}
	}
}