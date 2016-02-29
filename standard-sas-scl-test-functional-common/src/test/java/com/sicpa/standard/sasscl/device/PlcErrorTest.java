package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcVariables;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class PlcErrorTest extends AbstractFunctionnalTest {

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
		plc.firePlcEvent(new PlcEvent(PlcVariables.NTF_WAR_ERR_REGISTER.getVariableName(), 262144));
	}
}
