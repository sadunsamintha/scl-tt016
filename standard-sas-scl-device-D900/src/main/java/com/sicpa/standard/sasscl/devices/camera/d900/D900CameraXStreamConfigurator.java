package com.sicpa.standard.sasscl.devices.camera.d900;

import com.sicpa.standard.camera.d900.controller.model.D900CameraModel;
import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.devices.camera.d900.simulator.D900CameraSimulatorConfig;
import com.thoughtworks.xstream.XStream;

public class D900CameraXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {

	//	xstream.useAttributeFor(ProductCameraJob.class, "productId");

		xstream.alias("D900CameraSimulatorConfig", D900CameraSimulatorConfig.class);
		
		xstream.alias("D900CameraModel", D900CameraModel.class);
		
	}
}
