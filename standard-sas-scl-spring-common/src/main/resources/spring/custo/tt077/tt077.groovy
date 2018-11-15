package custo.tt077
import com.sicpa.tt077.scl.TT077Bootstrap
import com.sicpa.tt077.printer.simulator.TT077PrinterAdaptorSimulator

beans {
    def serverBehavior=props['remoteServer.behavior'].toUpperCase()
    def printerBehavior=props['printer.behavior'].toUpperCase()

    addAlias('bootstrapAlias', 'bootstrap')
    bootstrap(TT077Bootstrap) { b ->
        b.parent = ref('bootstrapAlias')
    }

    if(serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
        importBeans('spring/custo/tt077/tt077-server.groovy')
    }
    if(serverBehavior == "SIMULATOR") {
        importBeans('spring/custo/tt077/tt077-server-simulator.groovy')
    }

	importBeans('spring/custo/tt077/tt077-hrd.xml')

    printerSimulatorAdaptor(TT077PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
        b.scope='prototype'
    }


    if(printerBehavior == 'SIMULATOR') {
        addAlias('printerLeibinger','printerSimulatorAdaptor')
    }

}
