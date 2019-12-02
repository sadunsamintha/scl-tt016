package custo.tt080
import com.sicpa.tt080.scl.TT080Bootstrap
import com.sicpa.tt080.printer.simulator.TT080PrinterAdaptorSimulator

beans {
    importBeans('spring/custo/tt080/plc/tt080.plc-import.groovy')
	importBeans('spring/custo/tt080/tt080-view.xml')
	importBeans('spring/custo/tt080/tt080-provider.xml')
	importBeans('spring/custo/tt080/tt080-flowControl.xml')

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()
    def printerBehavior=props['printer.behavior'].toUpperCase()

    addAlias('bootstrapAlias', 'bootstrap')
    bootstrap(TT080Bootstrap) { b ->
        b.parent = ref('bootstrapAlias')
        productionBatchProvider = ref('productionBatchProvider')
    }

    if(serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
        importBeans('spring/custo/tt080/tt080-server.groovy')
    }
    if(serverBehavior == "SIMULATOR") {
        importBeans('spring/custo/tt080/tt080-server-simulator.groovy')
    }

    importBeans('spring/custo/tt080/tt080-production-config.groovy')
    importBeans('spring/custo/tt080/tt080-production-mode.groovy')
	importBeans('spring/custo/tt080/tt080-hrd.xml')

    printerSimulatorAdaptor(TT080PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
        b.scope='prototype'
    }

    if(printerBehavior == 'SIMULATOR') {
        addAlias('printerLeibinger','printerSimulatorAdaptor')
    }

}
