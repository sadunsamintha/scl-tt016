package com.sicpa.standard.sasscl.devices.plc;

public class AutomatedBeamStatus {
    private boolean isManualMode;
    private int beamConveyorHeight;

    public AutomatedBeamStatus(boolean isManualMode, int beamConveyorHeight) {
        this.isManualMode = isManualMode;
        this.beamConveyorHeight = beamConveyorHeight;
    }

    public boolean isManualMode() {
        return isManualMode;
    }

    public int getBeamConveyorHeight() {
        return beamConveyorHeight;
    }

    public void setManualMode(boolean manualMode) {
        this.isManualMode = manualMode;
    }

    public void setBeamConveyorHeight(int beamConveyorHeight) {
        this.beamConveyorHeight = beamConveyorHeight;
    }
}
