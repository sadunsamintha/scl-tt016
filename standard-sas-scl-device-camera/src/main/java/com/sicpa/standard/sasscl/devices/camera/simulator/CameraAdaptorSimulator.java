package com.sicpa.standard.sasscl.devices.camera.simulator;

import com.sicpa.standard.camera.controller.ICognexCameraController;
import com.sicpa.standard.camera.controller.model.ICameraModel;
import com.sicpa.standard.sasscl.devices.camera.CameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.transformer.IRoiCameraImageTransformer;

public class CameraAdaptorSimulator extends CameraAdaptor implements ICameraAdaptorSimulator {

	public CameraAdaptorSimulator() {
	}

	public CameraAdaptorSimulator(ICognexCameraController<? extends ICameraModel> controller,
			IRoiCameraImageTransformer roiCameraImageTransformer) {
		super(controller, roiCameraImageTransformer);
	}

	@Override
	public ICameraControllerSimulator getSimulatorController() {
		return (ICameraControllerSimulator) getController();
	}

}
