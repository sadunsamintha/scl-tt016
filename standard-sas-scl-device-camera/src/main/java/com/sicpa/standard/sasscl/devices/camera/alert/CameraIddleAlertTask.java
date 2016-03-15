package com.sicpa.standard.sasscl.devices.camera.alert;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraIddleAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledAlertTask;
import com.sicpa.standard.sasscl.devices.camera.CameraCodeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class CameraIddleAlertTask extends AbstractScheduledAlertTask {

	protected long previousTime;
	protected ProductionParameters productionParameters;
	protected CameraIddleAlertTaskModel model;

	public CameraIddleAlertTask() {
		super();
	}

	public void start() {
		if (getModel().getMaxInactiveTimeInSec() > 0) {
			super.start();
			previousTime = System.currentTimeMillis();
		}
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(this, MessageEventKey.Alert.TOO_MUCH_CAMERA_IDLE_TIME);
	}

	@Override
	protected boolean isAlertPresent() {
		if (getModel().getMaxInactiveTimeInSec() > 0) {
			long currentTime = System.currentTimeMillis();
			long delta = currentTime - previousTime;
			return delta >= 1000l * getModel().getMaxInactiveTimeInSec();
		} else
			return false;
	}

	@Override
	public void reset() {
		previousTime = System.currentTimeMillis();
	}

	@Subscribe
	public void receiveCameraCode(CameraCodeEvent evt) {
		previousTime = System.currentTimeMillis();
	}

	@Override
	protected boolean isEnabledDefaultImpl() {
		// disable if maintenance mode
		if (productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE)) {
			return false;
		}

		return getModel().isEnabled();
	}

	@Override
	public String getAlertName() {
		return "Camera too much idle time";
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setModel(CameraIddleAlertTaskModel model) {
		this.model = model;
	}

	public CameraIddleAlertTaskModel getModel() {
		return model;
	}

	@Override
	public long getDelay() {
		return model.getDelayInSec();
	}
}