package com.sicpa.standard.sasscl.devices.camera.d900.simulator;

import com.sicpa.standard.camera.d900.controller.ID900CameraController;
import com.sicpa.standard.camera.d900.controller.model.ID900CameraModel;
import com.sicpa.standard.sasscl.devices.camera.d900.D900CameraAdaptor;
import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CameraControllerSimulator;

public class D900CameraAdaptorSimulator extends D900CameraAdaptor implements ID900CameraAdaptorSimulator {

	public D900CameraAdaptorSimulator() {
	}

	public D900CameraAdaptorSimulator(ID900CameraController<? extends ID900CameraModel> controller) {
		super(controller);
	}

	@Override
	public ID900CameraControllerSimulator getSimulatorController() {
		return (ID900CameraControllerSimulator) getController();
	}

}
