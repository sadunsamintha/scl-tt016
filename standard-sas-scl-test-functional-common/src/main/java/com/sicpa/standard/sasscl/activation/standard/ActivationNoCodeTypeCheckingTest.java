package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.NoProductValidator;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class ActivationNoCodeTypeCheckingTest extends AbstractFunctionnalTest {

	public void test() {
		PropertyPlaceholderResources.addProperties("productValidator", NoProductValidator.class.getName());

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);
		runAllTasks();
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodeWithWrongCodeType();
		checkApplicationStatusRUNNING();

		stopProduction();
		checkApplicationStatusCONNECTED();

		exit();
	}


	public void generateCameraCodeWithWrongCodeType() {
		try {
			String code = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(8)).getEncryptedCodes(1).get(0);
			camera.fireGoodCode(code);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
