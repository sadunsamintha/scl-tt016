package com.sicpa.standard.sasscl.functionaltests.device;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class PlcErrorTest extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() {
		init();

		setProductionParameter();

		startProduction();
		checkApplicationStatusRUNNING();

		triggerPlcError();
		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.PLC.PLC_ERR_PLC_FAULT);

		exit();
	}

	public void triggerPlcError() {
		IPlcVariable<Integer> errorVar = BeanProvider.getBean("NTF_CAB_WAR_ERR_REGISTER_var");
		plc.firePlcEvent(new PlcEvent(errorVar.getVariableName(), 262144));
	}
}
