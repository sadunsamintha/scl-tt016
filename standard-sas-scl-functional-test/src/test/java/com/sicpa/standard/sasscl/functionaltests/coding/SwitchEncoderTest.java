package com.sicpa.standard.sasscl.functionaltests.coding;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

public class SwitchEncoderTest extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() throws RemoteServerException, CryptographyException {
		init();

		((RemoteServerSimulator) remoteServer).getSimulatorModel().setNumberOfCodesByEncoder(10);

		setProductionParameter();

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		trigProduct(15);

		checkApplicationStatusRUNNING();
		stopProduction();
		checkApplicationStatusCONNECTED();
		checkEncoderInfo();

		exit();
	}

	private void checkEncoderInfo() {
		boolean firstFound = false;
		boolean secondFound = false;
		for (EncoderInfo info : storage.getAllEndodersInfo()) {
			if (info.getCodeTypeId() == 1) {
				if (info.isFinished() && info.getSequence() == 10) {
					firstFound = true;
				} else if (!info.isFinished() && info.getSequence() == 5) {
					secondFound = true;
				}
			}
		}

		assertTrue("finished encoder is not found", firstFound);
		assertTrue("current encoder is not found", secondFound);
	}

	
}
