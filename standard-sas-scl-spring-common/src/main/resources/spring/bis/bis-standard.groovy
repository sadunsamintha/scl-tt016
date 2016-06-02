import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer


beans{

	bis(BisAdapter){ b->
		b.scope='prototype'
		controller = ref('bisRemoteServer')
		blockProduction = props['bis.disconnected.production.block']
	}

	bisRemoteServer(BisRemoteServer,ref('bisModel')){b->
		b.scope='prototype'
	}

	bisModel(ConfigUtils,profilePath+'/config/bis/bisConfig.xml'){ b->
		b.factoryMethod='load'
	}
}