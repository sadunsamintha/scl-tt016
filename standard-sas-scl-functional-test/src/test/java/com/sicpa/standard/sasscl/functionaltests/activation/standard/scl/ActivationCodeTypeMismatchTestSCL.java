package com.sicpa.standard.sasscl.functionaltests.activation.standard.scl;

import com.sicpa.standard.sasscl.activation.standard.ActivationCodeTypeMismatchTest;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationCodeTypeMismatchTestSCL extends ActivationCodeTypeMismatchTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);
	}
}
