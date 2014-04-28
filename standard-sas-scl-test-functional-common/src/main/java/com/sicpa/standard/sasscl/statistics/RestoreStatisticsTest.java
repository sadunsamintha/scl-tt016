package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.client.common.utils.SingleAppInstanceUtils;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.MainApp;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public abstract class RestoreStatisticsTest extends AbstractFunctionnalTest {

	public void test() throws RemoteServerException, CryptographyException {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20, 3);
		stopProduction();
		checkApplicationStatusCONNECTED();

		checkStatistics(20, 3);
		exit();

		SingleAppInstanceUtils.releaseLock();
		mainApp = new MainApp();
		loadSpring();

		generateCameraCodes(20, 3);
		checkStatistics(20, 3);

		exit();
	}

	@Override
	public void init() {
		super.init();
	}

	public void generateCameraCodes(int good, int bad) throws RemoteServerException, CryptographyException {

		IEncoder encoder = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(1));

		for (int i = 0; i < good; i++) {
			String code = encoder.getEncryptedCodes(1).get(0);
			camera.fireGoodCode(code);
		}

		for (int i = 0; i < bad; i++) {
			String code = "B00" + i;
			camera.fireBadCode(code);
		}
	}
}
