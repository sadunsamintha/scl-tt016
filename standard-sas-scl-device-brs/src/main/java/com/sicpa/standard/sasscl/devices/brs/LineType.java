package com.sicpa.standard.sasscl.devices.brs;

public enum LineType {
	DRINKS(1, 3), LABELLER(1, 0), TOBACCO_1(1, 0), TOBACCO_2(2, 0,true), LABELLER_2(2, 0,true), DRINKS_2(2, 6);

	protected final int masterCount;
	protected final int slaveCount;
	boolean masterOnSameLine;

	LineType(int masterCount, int slaveCount) {
		this.masterCount = masterCount;
		this.slaveCount = slaveCount;
	}

	LineType(int masterCount, int slaveCount, boolean masterOnSameLine) {
		this.masterCount = masterCount;
		this.slaveCount = slaveCount;
		this.masterOnSameLine = masterOnSameLine;
	}

	public boolean isMasterOnSameLine() {
		return masterOnSameLine;
	}

	public static int cameraCount(int index) {
		return values()[index].masterCount + values()[index].slaveCount;
	}

	public static int masterCount(int index) {
		return values()[index].masterCount;
	}

	public static boolean isMasterOnSameLine(int index) {
		return values()[index].isMasterOnSameLine();
	}
}
