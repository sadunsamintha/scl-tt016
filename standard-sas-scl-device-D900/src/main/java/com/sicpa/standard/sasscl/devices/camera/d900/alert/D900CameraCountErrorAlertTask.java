package com.sicpa.standard.sasscl.devices.camera.d900.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

/**
 * Implementation of a camera alert task   <br/>
 * Send a error alert when too many bad codes are received from the camera given a configurable threshold.
 */
public class D900CameraCountErrorAlertTask extends D900AbstractCameraCountAlertTask {

	private static final String ALERT_NAME = "Too many bad codes error alert";

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.TOO_MANY_CAMERA_ERROR);
	}
	@Override
	public String getAlertName() {
		return ALERT_NAME;
	}

}
