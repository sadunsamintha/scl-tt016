import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController
beans{

	plcSimulatorModel(ConfigUtils,profilePath+'/config/plc/simulator/plcSimulator.xml'){b->
		b.factoryMethod='load'
		b.scope='prototype'
	}

	plcController(PlcSimulatorController,ref('plcSimulatorModel')){b->
		b.scope='prototype'
		simulatorGui=ref('simulatorGui')
		reqStopVarName="#{plcVarMap['REQUEST_STOP']}"
		reqStartVarName="#{plcVarMap['REQUEST_START']}";
	}
}
