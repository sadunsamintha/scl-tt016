package com.sicpa.standard.sasscl.coding;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.utils.printer.PrinterSimulatorThatProvidesCodes;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;

public class SwitchEncoderTest extends AbstractFunctionnalTest {

	volatile boolean codeReceived = false;

	public void test() throws RemoteServerException, CryptographyException {
		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();
		for (int i = 0; i < 15; i++) {
			camera.readCode();
		}
		checkApplicationStatusRUNNING();
		stopProduction();
		checkApplicationStatusCONNECTED();

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

		exit();
	}

	public static class RemoteServerSmallEncoder extends RemoteServerSimulator {
		public RemoteServerSmallEncoder(String configFile) throws RemoteServerException {
			super(configFile);
		}

		private RemoteServerSmallEncoder(RemoteServerSimulatorModel model) throws RemoteServerException {
			super(model);
		}

		@Override
		public IEncoder createOneEncoder(int year, int codeTypeId) throws UnknownModeException,
				UnknownVersionException, UnknownSystemTypeException, SicpadataException, NoSuchPasswordException {

			IEncoder encoder = super.createOneEncoder(year, codeTypeId);
			((StdCryptoEncoderWrapperSimulator) encoder).setRemainingCode(10);
			return encoder;
		}
	}

	public static class CameraNoAutomaticRead extends CameraSimulatorController {

		public CameraNoAutomaticRead() {
			super();
		}

		public CameraNoAutomaticRead(CameraSimulatorConfig config) {
			super(config);
		}

		@Override
		protected Runnable createCameraThread() {
			return new Runnable() {
				@Override
				public void run() {

				}
			};
		}

	}

	private PrinterSimulatorThatProvidesCodes printerSimul;
	private IStorage storage;

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}

	@Override
	public void init() {
		PropertyPlaceholderResources.addProperties(BeansName.REMOTE_SERVER_SIMULATOR,
				RemoteServerSmallEncoder.class.getName());
		PropertyPlaceholderResources.addProperties(BeansName.CAMERA_SIMULATOR, CameraNoAutomaticRead.class.getName());
		super.init();

		storage = BeanProvider.getBean(BeansName.STORAGE);
	}

	@Override
	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);

		PrinterAdaptorSimulator printerDevice = (PrinterAdaptorSimulator) devicesMap.get("pr_scl_1");
		printerSimul = (PrinterSimulatorThatProvidesCodes) printerDevice.getSimulatorController();
		printerSimul.setCodeByRequest(1);
	}

}
