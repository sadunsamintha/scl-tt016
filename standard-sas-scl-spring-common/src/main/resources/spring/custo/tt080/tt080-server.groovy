package custo.tt080

import com.sicpa.tt080.remote.server.TT080RemoteServer

beans{

    addAlias('remoteServerAlias','remoteServer')
    remoteServer(TT080RemoteServer){b->
        b.parent = ref('remoteServerAlias')
    }

}