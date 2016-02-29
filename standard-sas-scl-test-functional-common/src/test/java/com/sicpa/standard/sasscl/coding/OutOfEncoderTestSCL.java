package com.sicpa.standard.sasscl.coding;

import org.mockito.Mockito;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class OutOfEncoderTestSCL extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() {
		init();

		setProductionParameter();
		checkApplicationStatusCONNECTED();
		startProduction();
		checkApplicationStatusRUNNING();

		// first time it will empty all encoder
		doEmptyEncoder();
		// next time encoder empty exception
		doEmptyEncoder();

		runAllTasks();

		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE);
		exit();
	}

	private void doEmptyEncoder() {
		coding.askCodes(5000000, Mockito.mock(ICodeReceiver.class));
	}
}
