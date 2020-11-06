package custo.tt065

import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator
import com.sicpa.tt065.brs.sku.TT065CompliantProductSkuResolver
import com.sicpa.tt065.printer.simulator.TT065PrinterAdaptorSimulator
import com.sicpa.tt065.remote.impl.dtoconverter.TT065SkuConverter
import com.sicpa.tt065.remote.simulator.TT065RemoteServerSimulator
import com.sicpa.tt065.scl.TT065Bootstrap
import com.sicpa.tt065.redlight.TT065RedLightService
import com.sicpa.tt065.devices.brs.TT065BrsBarcodeCheck

/**
 * Created by mjimenez on 15/09/2016.
 */

beans{
    importBeans('spring/custo/tt065/plc/tt065.plc-import.groovy')
    importBeans('spring/custo/tt065/tt065-provider.xml')
    importBeans('spring/custo/tt065/tt065-view.xml')
    importBeans('spring/custo/tt065/tt065-flowControl.xml')

    addAlias('bootstrapAlias','bootstrap')
    bootstrap(TT065Bootstrap){ b->
        b.parent=ref('bootstrapAlias')
        productionBatchProvider=ref('productionBatchProvider')
    }

    importBeans('spring/custo/tt065/tt065-activation.xml')
    importBeans('spring/custo/tt065/tt065-scheduler.xml')

    def serverBehavior=props['remoteServer.behavior'].trim().toUpperCase()
    def printerBehavior=props['printer.behavior'].trim().toUpperCase()

    importBeans('spring/server/server-core5.groovy')

    if(serverBehavior == "STANDARD") {
        importBeans('spring/custo/tt065/tt065-server.groovy')

        skuConverter(TT065SkuConverter) {
            productionModeMapping=ref('productionModeMapping')
        }

        compliantProduct(TT065CompliantProductSkuResolver)
    } else {
        remoteServer(TT065RemoteServerSimulator, ref('simulatorRemoteModel')) {
            simulatorGui = ref('simulatorGui')
            cryptoFieldsConfig = ref('cryptoFieldsConfig')
            productionParameters = ref('productionParameters')
            serviceProviderManager = ref('cryptoProviderManager')
            storage = ref('storage')
            fileSequenceStorageProvider = ref('fileSequenceStorageProvider')
            remoteServerSimulatorOutputFolder = profilePath + '/simulProductSend'
            cryptoMode = props['server.simulator.cryptoMode'].trim()
            cryptoModelPreset = props['server.simulator.cryptoModelPreset'].trim()
            hrdEnable = props['hrd.enable'].trim()
        }


    }

    redLightService(TT065RedLightService){
        remoteServer = ref('remoteServer')
    }

    importBeans('spring/custo/tt065/tt065-hrd.xml')

    printerSimulatorAdaptor(TT065PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
        b.scope='prototype'
    }

    if(printerBehavior == 'SIMULATOR') {
        addAlias('printerLeibinger','printerSimulatorAdaptor')
    }
    brsBarcodeCheck(TT065BrsBarcodeCheck){ compliantProductResolver=ref('compliantProduct') }
}