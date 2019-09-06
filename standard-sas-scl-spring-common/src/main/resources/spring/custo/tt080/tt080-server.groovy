package custo.tt080

import com.sicpa.tt080.remote.server.TT080RemoteServer
import com.sicpa.tt080.sasscl.devices.remote.impl.TT080PackageSenderGlobal

beans{

    addAlias('remoteServerAlias','remoteServer')
    remoteServer(TT080RemoteServer){b->
        b.parent = ref('remoteServerAlias')
    }

    packageSender(TT080PackageSenderGlobal){
        converter=ref('dtoConverter')
        remoteServices=ref('remoteServices')
    }
}