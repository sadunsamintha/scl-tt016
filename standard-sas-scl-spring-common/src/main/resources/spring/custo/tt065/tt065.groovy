package custo.tt065

import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator
import com.sicpa.tt065.scl.TT065Bootstrap
import com.sicpa.tt065.remote.simulator.TT065RemoteServerSimulator
import com.sicpa.tt065.printer.simulator.TT065PrinterAdaptorSimulator
/**
 * Created by mjimenez on 15/09/2016.
 */

beans{
    importBeans('spring/custo/tt065/plc/tt065.plc-import.groovy')

    addAlias('bootstrapAlias','bootstrap')
    bootstrap(TT065Bootstrap){ b->
        b.parent=ref('bootstrapAlias')
    }

    importBeans('spring/custo/tt065/tt065-view.xml')
    importBeans('spring/custo/tt065/tt065-provider.xml')
    importBeans('spring/custo/tt065/tt065-flowControl.xml')



    def serverBehavior=props['remoteServer.behavior'].toUpperCase()


    if(serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
    } else {
        remoteServer(TT065RemoteServerSimulator,ref('simulatorRemoteModel')){
            simulatorGui=ref('simulatorGui')
            cryptoFieldsConfig=ref('cryptoFieldsConfig')
            productionParameters=ref('productionParameters')
            serviceProviderManager=ref('cryptoProviderManager')
            storage=ref('storage')
            fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
            remoteServerSimulatorOutputFolder = profilePath+'/simulProductSend'
            cryptoMode=props['server.simulator.cryptoMode']
            cryptoModelPreset=props['server.simulator.cryptoModelPreset']
        }
    }

    importBeans('spring/custo/tt065/tt065-hrd.xml')

    printerSimulatorAdaptor(TT065PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
        b.scope='prototype'
    }



}