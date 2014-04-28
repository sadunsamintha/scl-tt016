package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class DisconnectionTest extends AbstractFunctionnalTest {

	public void test() throws DeviceException {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

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
