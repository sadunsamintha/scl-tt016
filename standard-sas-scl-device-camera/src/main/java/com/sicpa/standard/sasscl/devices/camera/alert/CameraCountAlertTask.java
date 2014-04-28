package com.sicpa.standard.sasscl.devices.camera.alert;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledBadCountAlertTask;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;

/**
 * implementation of an alert task for the camera<br/>
 * send an alert when too many bad codes are received from the camera
 * 
 * @author DIelsch
 * 
 */
public class CameraCountAlertTask extends AbstractScheduledBadCountAlertTask {

	protected ProductionParameters productionParameters;
	protected CameraCountAlertTaskModel model;

	public CameraCountAlertTask() {
		super();
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.TOO_MUCH_CAMERA_ERROR);
	}

	@Subscribe
	public void receiveCameraCode(final CameraGoodCodeEvent evt) {
		increaseGood();
	}

	@Subscribe
	public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
		increaseBad();
	}

	@Override
	protected boolean isEnabled() {
		if (!productionParameters.getProductionMode().isWithSicpaData()) {
			return false;
		}
		return model.isEnabled();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public String getAlertName() {
		return "Too many bad codes alert";
	}

	@Override
	public long getDelay() {
		return getModel().getDelay() * 1000;
	}

	public void setModel(CameraCountAlertTaskModel model) {
		this.model = model;
	}

	public CameraCountAlertTaskModel getModel() {
		return model;
	}

	@Override
	public int getThreshold() {
		return model.getMaxUnreadCount();
	}

	@Override
	public int getSampleSize() {
		return model.getSampleSize();
	}
}
