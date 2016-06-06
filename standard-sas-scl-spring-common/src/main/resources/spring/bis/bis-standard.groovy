import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer


beans{

	bis(BisAdapter){ b->
		b.scope='prototype'
		controller = ref('bisRemoteServer')
		blockProduction = props['bis.disconnected.production.block']
	}

	bisRemoteServer(BisRemoteServer){b->
		b.scope='prototype'
		ip=props['bis.ip']
		port=props['bis.port']
		recognitionResultRequestIntervalMs=props['bis.recognitionResultRequestIntervalMs']
		unknownSkuId=props['bis.unknownSkuId']
		displayAlertMessage=props['bis.displayAlertMessage']
	}

}