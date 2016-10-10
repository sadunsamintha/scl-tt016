package custo.tt065

import com.sicpa.tt065.scl.TT065Bootstrap
/**
 * Created by mjimenez on 15/09/2016.
 */

beans{
    addAlias('bootstrapAlias','bootstrap')
    bootstrap(TT065Bootstrap){ b->
        b.parent=ref('bootstrapAlias')
    }

    def serverBehavior=props['remoteServer.behavior'].toUpperCase()

    if(serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
    }

}