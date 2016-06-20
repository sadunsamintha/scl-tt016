import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer
import com.sicpa.standard.sasscl.devices.bis.simulator.BisSimulatorAdaptor;
import com.sicpa.standard.sasscl.skureader.SkuFinder;


beans{

	bis(BisSimulatorAdaptor){ b->
		b.scope='prototype'
		blockProduction = props['bis.disconnected.production.block']
		skuFinder=ref('skuFinder')
		skuBisProvider=ref('skuBisProvider')
		simulatorGui=ref('simulatorGui')
	}

}