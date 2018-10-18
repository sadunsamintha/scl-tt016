import com.sicpa.tt077.remote.server.TT077RemoteServer

beans{

    addAlias('remoteServerAlias','remoteServer')
    remoteServer(TT077RemoteServer){b->
        b.parent = ref('remoteServerAlias')
    }

}