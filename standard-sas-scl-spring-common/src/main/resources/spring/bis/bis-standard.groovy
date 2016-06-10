import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer
import com.sicpa.standard.sasscl.skureader.SkuFinder;


beans{

	bis(BisAdapter){ b->
		b.scope='prototype'
		controller = ref('bisRemoteServer')
		blockProduction = props['bis.disconnected.production.block']
		skuFinder=ref('skuFinder')
		unknownSkuId=props['bis.unknownSkuId']
		displayAlertMessage=props['bis.displayAlertMessage']
	}

	bisRemoteServer(BisRemoteServer){b->
		b.scope='prototype'
		b.initMethod='init'
		ip=props['bis.ip']
		port=props['bis.port']
		recognitionResultRequestIntervalMs=props['bis.recognitionResultRequestIntervalMs']
	}

	skuFinder(SkuFinder){
		skuListProvider=ref('skuListProvider')
		productionParameters=ref('productionParameters')
	}
}