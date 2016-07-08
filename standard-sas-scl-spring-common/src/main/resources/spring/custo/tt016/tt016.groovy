import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.controller.flow.ProductStatusMerger

beans{
	def serverBehavior=props['remoteServer.behavior'].toUpperCase()
	def plcBehavior=props['plc.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	}

	if(plcBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016Plc.xml')
	} else {
		importBeans('spring/custo/tt016/tt016PlcSimulator.xml')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
	}

	productStatusMerger(ProductStatusMerger) {
		plcCameraResultIndexManager=ref('plcCameraResultIndexManager')
	}

	importBeans('spring/custo/tt016/tt016View.xml')
	importBeans('spring/custo/tt016/tt016Activation.xml')
	importBeans('spring/custo/tt016/tt016Statistics.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

}