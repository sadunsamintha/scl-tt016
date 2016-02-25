import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSecureAdaptorSimulator

beans{


	plcSecureController(PlcSimulatorController,ref('plcSimulatorModel')){ simulatorGui=ref('simulatorGui') }


	plcSecureAdaptor(PlcSecureAdaptorSimulator,ref('plcSecureController')){
		userIdStorage=ref('userIdStorage')
		securityModel=ref('securityModel')
	}
}
