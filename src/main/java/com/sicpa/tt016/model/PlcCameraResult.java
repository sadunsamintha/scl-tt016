package com.sicpa.tt016.model;

public class PlcCameraResult {

	private byte encryptedCodeLastByte;
	private int index;
	private int decodeTimeMs;
	private PlcCameraProductStatus plcCameraProductStatus;

	public PlcCameraResult(byte encryptedCodeLastByte, int index, int decodeTimeMs, PlcCameraProductStatus
			plcCameraProductStatus) {
		this.encryptedCodeLastByte = encryptedCodeLastByte;
		this.index = index;
		this.decodeTimeMs = decodeTimeMs;
		this.plcCameraProductStatus = plcCameraProductStatus;
	}

	public byte getEncryptedCodeLastByte() {
		return encryptedCodeLastByte;
	}

	public void setEncryptedCodeLastByte(byte encryptedCodeLastByte) {
		this.encryptedCodeLastByte = encryptedCodeLastByte;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getDecodeTimeMs() {
		return decodeTimeMs;
	}

	public void setDecodeTimeMs(int decodeTimeMs) {
		this.decodeTimeMs = decodeTimeMs;
	}

	public PlcCameraProductStatus getPlcCameraProductStatus() {
		return plcCameraProductStatus;
	}

	public void setPlcCameraProductStatus(PlcCameraProductStatus plcCameraProductStatus) {
		this.plcCameraProductStatus = plcCameraProductStatus;
	}
}
