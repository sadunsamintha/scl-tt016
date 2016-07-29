package com.sicpa.standard.sasscl.devices.camera.alert;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledBadCountAlertTask;
import com.sicpa.standard.sasscl.devices.camera.CameraCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.function.Function;

/**
 * Implementation of a camera alert task   <br/>
 * Send a error alert when too many bad codes are received from the camera given a configurable threshold.
 */
public class CameraCountErrorAlertTask extends AbstractCameraCountAlertTask {

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
