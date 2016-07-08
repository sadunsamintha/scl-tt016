package com.sicpa.tt016.model.event;

import com.sicpa.tt016.model.PlcCameraProductStatus;

public class PlcCameraResultEvent {

	private int encryptedCodeLastByte;
	private int index;
	private int decodeTimeMs;
	private PlcCameraProductStatus plcCameraProductStatus;

	public PlcCameraResultEvent(int encryptedCodeLastByte, int index, int decodeTimeMs, PlcCameraProductStatus
			plcCameraProductStatus) {
		this.encryptedCodeLastByte = encryptedCodeLastByte;
		this.index = index;
		this.decodeTimeMs = decodeTimeMs;
		this.plcCameraProductStatus = plcCameraProductStatus;
	}

	public int getEncryptedCodeLastByte() {
		return encryptedCodeLastByte;
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
