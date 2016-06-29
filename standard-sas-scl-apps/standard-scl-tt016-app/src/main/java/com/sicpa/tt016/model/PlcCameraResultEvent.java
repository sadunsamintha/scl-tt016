package com.sicpa.tt016.model;

public class PlcCameraResultEvent {

	private int index;
	private int decodeTimeMs;
	private PlcCameraProductStatus plcCameraProductStatus;

	public PlcCameraResultEvent(int index, int decodeTimeMs, PlcCameraProductStatus plcCameraProductStatus) {
		this.index = index;
		this.decodeTimeMs = decodeTimeMs;
		this.plcCameraProductStatus = plcCameraProductStatus;
	}

	public int getIndex() {
		return index;
	}

	public int getDecodeTimeMs() {
		return decodeTimeMs;
	}

	public PlcCameraProductStatus getPlcCameraProductStatus() {
		return plcCameraProductStatus;
	}
}
