package com.sicpa.tt018.scl.camera.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.camera.CameraCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.tt018.scl.utils.AlbaniaBlobUtils;

public class AlbaniaCameraCountAlertTask extends CameraCountAlertTask {

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaCameraCountAlertTask.class);

	private AlbaniaBlobUtils blobUtils;

	public AlbaniaCameraCountAlertTask() {
		super();
		setCodeValidator((evt) -> isValidCode(evt));
	}

	private boolean isValidCode(CameraCodeEvent evt) {
		return isValidCodeDefaultImpl(evt) || isBlobEnabledAndDetected(evt);
	}

	private boolean isBlobEnabledAndDetected(CameraCodeEvent evt) {
		boolean res = blobUtils.isBlobEnable() && blobUtils.isBlobDetected(evt.getCode());
		if (res) {
			logger.debug("Blob Detection event received {}", evt.toString());
		} else {
			logger.debug("Error event received {}", evt.toString());
		}
		return res;
	}

	public void setBlobUtils(AlbaniaBlobUtils blobUtils) {
		this.blobUtils = blobUtils;
	}

}
