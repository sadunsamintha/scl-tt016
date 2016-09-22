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

//    addAlias('cameraAlias','camera')
//    bootstrap(TT065Bootstrap){ b->
//        b.parent=ref('cameraAlias')
//    }

}