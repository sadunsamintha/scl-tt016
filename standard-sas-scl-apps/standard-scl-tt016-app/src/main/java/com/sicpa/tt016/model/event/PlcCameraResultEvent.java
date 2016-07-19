package com.sicpa.tt016.model.event;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;

public class PlcCameraResultEvent {

	private PlcCameraResult plcCameraResult;

	public PlcCameraResultEvent(PlcCameraResult plcCameraResult) {
		this.plcCameraResult = plcCameraResult;
	}

	public PlcCameraResult getPlcCameraResult() {
		return plcCameraResult;
	}

	public void setPlcCameraResult(PlcCameraResult plcCameraResult) {
		this.plcCameraResult = plcCameraResult;
	}
}
