package com.sicpa.standard.sasscl.skucheck;

import com.thoughtworks.xstream.XStream;

public class SkuCheckConfig {

	public static void main(String[] args) {
		XStream x = new XStream();

		SkuCheckConfig scs = new SkuCheckConfig();
		scs.trigValue = 10;
		scs.bufferSize = 50;
		scs.devicesCount = 1;
		System.out.println(x.toXML(scs));
	}

	protected int trigValue;
	protected int bufferSize;
	protected int devicesCount;

	// range 0-100 confidence over which we consider that there is no error when doing the check
	protected int confidenceLevelOk;

	// range 0-100
	// if confidence>=confidenceLevelCritical => small error
	// if confidence<confidenceLevelCritical critical error
	protected int confidenceLevelCritical;

	public int getTrigValue() {
		return trigValue;
	}

	public void setTrigValue(int trigValue) {
		this.trigValue = trigValue;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setDevicesCount(int devicesCount) {
		this.devicesCount = devicesCount;
	}

	public int getDevicesCount() {
		return devicesCount;
	}

	public int getConfidenceLevelCritical() {
		return confidenceLevelCritical;
	}

	public int getConfidenceLevelOk() {
		return confidenceLevelOk;
	}

	public void setConfidenceLevelCritical(int confidenceLevelCritical) {
		this.confidenceLevelCritical = confidenceLevelCritical;
	}

	public void setConfidenceLevelOk(int confidenceLevelOk) {
		this.confidenceLevelOk = confidenceLevelOk;
	}
}
