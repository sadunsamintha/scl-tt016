import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer


beans{

	bis(BisAdapter){ b->
		b.parent=ref('bisParent')
		b.scope='prototype'
		controller=ref('bisRemoteServer')
	}

	bisRemoteServer(BisRemoteServer){b->
		b.scope='prototype'
		b.initMethod='init'
		ip=props['bis.ip'].trim()
		port=props['bis.port'].trim()
		connectionLifeCheckIntervalSec=props['bis.connectionLifeCheckInterval.sec'].trim()
	}
}