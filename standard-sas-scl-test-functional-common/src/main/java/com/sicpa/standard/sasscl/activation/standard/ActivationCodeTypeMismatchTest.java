package com.sicpa.standard.sasscl.activation.standard;

import junit.framework.Assert;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.CodeTypeValidator;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public abstract class ActivationCodeTypeMismatchTest extends AbstractFunctionnalTest {

	private static final int WRONG_CODE_TYPE = 8;

	public void test() {

		PropertyPlaceholderResources.addProperties("productValidator", CodeTypeValidator.class.getName());

		init();

		setProductionParameter();
		runAllTasks();
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodeWithWrongCodeType();

		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Activation.EXCEPTION_CODE_TYPE_MISMATCH);

		exit();
		checkDataSentToRemoteServer();
	}

	public void generateCameraCodeWithWrongCodeType() {
		try {
			IEncoder encoder = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(WRONG_CODE_TYPE));
			String code = encoder.getEncryptedCodes(1).get(0);

			camera.fireGoodCode(code);
			dataGenerated.add("TYPE_MISMATCH" + code + "SKU#1");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
