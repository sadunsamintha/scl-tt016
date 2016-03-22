package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.NoProductValidator;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.model.CodeType;

public abstract class ActivationNoCodeTypeCheckingTest extends AbstractFunctionnalTest {

	private static final int WRONG_CODE_TYPE = 8;

	public void test() {
		PropertyPlaceholderResources.addProperties("productValidator", NoProductValidator.class.getName());

		init();

		setProductionParameter();
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
			String code = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(WRONG_CODE_TYPE))
					.getEncryptedCodes(1).get(0);
			camera.fireGoodCode(code);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
