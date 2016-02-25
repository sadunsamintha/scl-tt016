import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSecureAdaptorSimulator
import com.sicpa.standard.plc.controller.internal.PlcControllerImpl
import com.sicpa.standard.sasscl.devices.plc.impl.PlcSecureAdaptor
import com.sicpa.standard.plc.controller.model.PlcModel

beans{


	plcSecureModel(ConfigUtils,profilePath+'/config/plc/plcSecure.xml'){b->
		b.factoryMethod='load'
		lifeCheckRequest=ref('secureModuleLifecheckVariable')
	}

	plcSecureController(PlcControllerImpl) { model=ref('plcSecureModel') }


	plcSecureAdaptor(PlcSecureAdaptor,ref('plcSecureController')){
		userIdStorage=ref('userIdStorage')
		securityModel=ref('securityModel')
		notificationVariables= ref('plcSecureNotifications')
		plcSecureRequestMap= ref('plcSecureRequestAction')
		parameters= ref('plcSecureParameters')
		userIdStorage= ref('userIdStorage')
		securityModel= ref('securityModel')
	}
}
