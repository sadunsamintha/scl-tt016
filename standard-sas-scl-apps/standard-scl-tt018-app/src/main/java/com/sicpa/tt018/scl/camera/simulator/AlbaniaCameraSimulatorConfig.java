package com.sicpa.tt018.scl.camera.simulator;

import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;

public class AlbaniaCameraSimulatorConfig extends CameraSimulatorConfig {


    private static final long serialVersionUID = 2490178576036645099L;

    private int percentageBlobCode = 0;

    public int getPercentageBlobCode() {
        return percentageBlobCode;
    }

    public void setPercentageBlobCode(int percentageBlobCode) {
        this.percentageBlobCode = percentageBlobCode;
    }

}
