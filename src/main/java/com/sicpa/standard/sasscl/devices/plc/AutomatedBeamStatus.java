package com.sicpa.standard.sasscl.devices.plc;

public class AutomatedBeamStatus {
    private boolean isManualMode;
    private int beamToleranceHeight;

    public AutomatedBeamStatus(boolean isManualMode, int beamToleranceHeight) {
        this.isManualMode = isManualMode;
        this.beamToleranceHeight = beamToleranceHeight;
    }

    public boolean isManualMode() {
        return isManualMode;
    }

    public int getBeamToleranceHeight() {
        return beamToleranceHeight;
    }

    public void setManualMode(boolean manualMode) {
        this.isManualMode = manualMode;
    }

    public void setBeamToleranceHeight(int beamToleranceHeight) {
        this.beamToleranceHeight = beamToleranceHeight;
    }
}
