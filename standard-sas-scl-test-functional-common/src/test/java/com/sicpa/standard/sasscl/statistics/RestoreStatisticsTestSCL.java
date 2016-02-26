package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class RestoreStatisticsTestSCL extends RestoreStatisticsTest {

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfig sc = new SpringConfigSCL();
		sc.remove(SpringConfigSCL.POST_PACKAGE);
		return sc;
	}

	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.none);
	}
}
