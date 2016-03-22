package com.sicpa.standard.sasscl.functionaltests.device;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class DisconnectionTest extends AbstractFunctionnalTest {
	
	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() throws DeviceException {

		init();

		setProductionParameter();

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		disconnectCamera();
		disconnectPlc();

		checkApplicationStatusRECOVERING();

		connectCamera();
		connectPlc();

		checkApplicationStatusCONNECTED();

	}

}
