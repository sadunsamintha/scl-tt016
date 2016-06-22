import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.bis.BisAdapter
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer
import com.sicpa.standard.sasscl.devices.bis.simulator.BisControllerSimulator;
import com.sicpa.standard.sasscl.devices.bis.simulator.BisSimulatorAdaptor;
import com.sicpa.standard.sasscl.skureader.SkuFinder;


beans{

	bis(BisSimulatorAdaptor){ b->
		b.parent=ref('bisParent')
		b.scope='prototype'
		simulatorGui=ref('simulatorGui')
		controller = ref('bisControllerSimulator')
	}
	
	bisControllerSimulator(BisControllerSimulator)
}