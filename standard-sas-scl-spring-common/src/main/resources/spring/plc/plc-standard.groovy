import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.plc.controller.internal.PlcControllerImpl
import com.sicpa.standard.plc.controller.model.LifeCheckType;
import com.sicpa.standard.plc.controller.model.PlcExecutionMode;
import com.sicpa.standard.plc.controller.model.PlcModel;
beans{


	stdPlcModel(PlcModel){
		driverName='JBeckPlcDriver'
		lifeCheckType=LifeCheckType.ON_READ_WRITE_VALUE_SYNC_CHECK
		littleEndian=true
		useSimulator=false
		executionMode=PlcExecutionMode.MULTITHREADED
		ip=props['plc.ip']
		lifeCheckRequest=ref('REQUEST_LIFE_CHECK_var')
	}

	plcController(PlcControllerImpl){b->
		b.scope='prototype'
		model=ref('stdPlcModel')
	}
}
