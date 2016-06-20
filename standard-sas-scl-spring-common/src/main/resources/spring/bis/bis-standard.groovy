import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer


beans{

	bis(BisAdapter){ b->
		b.scope='prototype'
		controller = ref('bisRemoteServer')
		blockProduction = props['bis.disconnected.production.block']
		skuFinder=ref('skuFinder')
		unknownSkuId=props['bis.unknownSkuId']
		displayAlertMessage=props['bis.displayAlertMessage']
		skuBisProvider=ref('skuBisProvider')
	}

	bisRemoteServer(BisRemoteServer){b->
		b.scope='prototype'
		b.initMethod='init'
		ip=props['bis.ip']
		port=props['bis.port']
		connectionLifeCheckIntervalSec=props['bis.connectionLifeCheckInterval.sec']
	}
}