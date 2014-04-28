package com.sicpa.standard.sasscl.activation.standard;

import java.util.ArrayList;
import java.util.List;

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
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class ActivationTestSCL extends ActivationTest implements ICameraControllerListener {

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
		// trig the need code from the printer
		// camera.readCode();
		// ThreadUtils.sleepQuietly(5000);

		cameraModel.setPercentageBadCode(0);
		for (int i = 0; i < good; i++) {
			camera.readCode();
		}

		cameraModel.setPercentageBadCode(100);
		for (int i = 0; i < bad; i++) {
			camera.readCode();
		}
	}

	@Override
	public void init() {
		PropertyPlaceholderResources.addProperties(BeansName.CAMERA_SIMULATOR, CameraSimuNoRead.class.getName());
		super.init();
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
		dataGenerated.add("UNREAD" + "SKU#1");
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
}
