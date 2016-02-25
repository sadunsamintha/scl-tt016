import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController
import com.sicpa.standard.plc.controller.internal.PlcControllerImpl
beans{

	stdPlcModel(ConfigUtils,profilePath+'/config/plc/plc.xml'){b->
		b.factoryMethod='load'
		b.scope='prototype'
		lifeCheckRequest=ref('lifeCheckVar')
	}

	plcController(PlcControllerImpl,ref('plcSimulatorModel')){b->
		b.scope='prototype'
		model=ref('stdPlcModel')
	}
}
