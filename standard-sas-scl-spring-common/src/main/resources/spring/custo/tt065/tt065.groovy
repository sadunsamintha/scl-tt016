package custo.tt065

import com.sicpa.tt065.scl.TT065Bootstrap
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
    }

}