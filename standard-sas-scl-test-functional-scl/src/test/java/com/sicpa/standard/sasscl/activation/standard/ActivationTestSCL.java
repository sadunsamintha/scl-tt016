package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.camera.controller.ICameraController;
import com.sicpa.standard.camera.controller.ICameraControllerListener;
import com.sicpa.standard.camera.driver.event.CameraDriverEventArgs;
import com.sicpa.standard.camera.driver.event.CodeReceivedEventArgs;
import com.sicpa.standard.camera.driver.event.ImageReceivedEventArgs;
import com.sicpa.standard.camera.parser.event.CodeEventArgs;
import com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs;
import com.sicpa.standard.camera.parser.event.MetricsEventArgs;
import com.sicpa.standard.camera.parser.event.UnknownCodeEventArgs;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.printer.IPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivationTestSCL extends ActivationTest implements ICameraControllerListener {
	protected IPrinterAdaptor printer;

	public static class CameraSimuNoRead extends CameraSimulatorController {
		@Override
		protected Runnable createCameraThread() {
			return new Runnable() {
				public void run() {
					System.out.println();
				}
			};
		}

		public CameraSimuNoRead() {
			super();
		}

		public CameraSimuNoRead(CameraSimulatorConfig config) {
			super(config);
		}
	}

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}

	final List<String> codes = new ArrayList<String>();

	@Override
	protected void configureDevices() {
		super.configureDevices();
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);
		printer = (IPrinterAdaptor) devicesMap.get("pr_scl_1");

		final ICoding coding = BeanProvider.getBean(BeansName.CODING);
		coding.addCodeReceiver(new ICodeReceiver() {

			@Override
			public void provideCode(List<String> c, Object o) {
				codes.addAll(c);
			}
		});
		camera.addListener(this);
	}

	public void generateCameraCodes(int good, int bad) {
		cameraModel.setPercentageBadCode(0);
		for (int i = 0; i < good; i++) {
			((TestPrinter) printer).askForCodes(1);
			camera.readCode();
		}

		cameraModel.setPercentageBadCode(100);
		for (int i = 0; i < bad; i++) {
			((TestPrinter) printer).askForCodes(1);
			camera.readCode();
		}
	}

	@Override
	public void init() {
		PropertyPlaceholderResources.addProperties(BeansName.CAMERA_SIMULATOR, CameraSimuNoRead.class.getName());
		PropertyPlaceholderResources.addProperties("printerSimulatorAdaptor", TestPrinter.class.getName());
		removeDataDirectories();
		super.init();
	}

	protected void removeDataDirectories() {
		File internalDirectory = new File("internalSimulator");
		File monitoringDirectory = new File("monitoring");
		File dataDirectory = new File("dataSimulator");
		deleteDir(internalDirectory);
		deleteDir(monitoringDirectory);
		deleteDir(dataDirectory);
	}

	public boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i=0; i<children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		// The directory is now empty or this is a file so delete it
		return dir.delete();
	}

	int counter = 0;

	@Override
	public void onValidCameraCodeReceived(ICameraController<?> arg0, CodeEventArgs arg1) {
		dataGenerated.add("AUTHENTICATED" + arg1.getCode() + "SKU#1");
		counter++;
	}

	@Override
	public void onUnknownCameraCodeReceived(ICameraController<?> arg0, UnknownCodeEventArgs arg1) {

	}

	@Override
	public void onMetricsReceived(ICameraController<?> arg0, MetricsEventArgs arg1) {

	}

	@Override
	public void onErrorCameraCodeReceived(ICameraController<?> arg0, ErrorCodeEventArgs arg1) {
		dataGenerated.add("SENT_TO_PRINTER_UNREAD" + codes.get(counter) + "SKU#1");
		counter++;
	}

	@Override
	public void onCameraImageReceived(ICameraController<?> arg0, ImageReceivedEventArgs arg1) {

	}

	@Override
	public void onCameraDriverEventReceived(ICameraController<?> arg0, CameraDriverEventArgs arg1) {

	}

	@Override
	public void onCameraCodeReceived(ICameraController<?> arg0, CodeReceivedEventArgs arg1) {

	}

	private static class TestPrinter extends PrinterAdaptorSimulator {
		List<String> codes;

		public TestPrinter(IPrinterController controller) {
			super(controller);
		}


		@Override
		public void sendCodesToPrint(final List<String> codes) {
			this.codes = codes;
		}

		@Override
		public void doStart() throws PrinterAdaptorException {
			fireDeviceStatusChanged(DeviceStatus.STARTED);
		}

		@Override
		public void doStop() throws PrinterAdaptorException {
			fireDeviceStatusChanged(DeviceStatus.STOPPED);
		}

		@Override
		public String requestCode() {
			return codes.get(0);
		}

		public void askForCodes(int nbCodes) {
			super.notifyRequestCodesToPrint(nbCodes);
		}
	}
}
