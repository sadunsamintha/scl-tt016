package com.sicpa.tt018.scl.camera.simulator;

import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;

@SuppressWarnings("serial")
public class AlbaniaCameraSimulatorConfig extends CameraSimulatorConfig {

	private int percentageBlobCode = 0;

	public int getPercentageBlobCode() {
		return percentageBlobCode;
	}

	public void setPercentageBlobCode(int percentageBlobCode) {
		this.percentageBlobCode = percentageBlobCode;
	}

}
