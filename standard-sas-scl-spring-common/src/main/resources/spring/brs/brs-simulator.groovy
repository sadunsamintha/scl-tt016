import com.sicpa.standard.sasscl.devices.brs.simulator.BrsAdaptorSimulator
import com.sicpa.standard.client.common.utils.ConfigUtils


beans{

	brsAdaptor(BrsAdaptorSimulator,ref('simulatorBrsModel'),ref('productionParameters'),props['brs.behavior']){b->
		b.scope='prototype'
	}

	simulatorBrsModel(ConfigUtils, profilePath+'/config/brs/simulator/brsSimulator.xml'){b->
		b.factoryMethod='load'
	}
}