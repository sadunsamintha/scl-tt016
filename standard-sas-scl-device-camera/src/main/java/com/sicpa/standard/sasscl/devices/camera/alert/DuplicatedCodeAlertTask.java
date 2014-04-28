package com.sicpa.standard.sasscl.devices.camera.alert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraDuplicatedAlertTaskModel;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;

/**
 * Implementation of an alert task for the camera<br/>
 * send a alert when duplicate code is more than the defined threshold during scanning process.
 * 
 */
public class DuplicatedCodeAlertTask extends AbstractAlertTask {

	protected ProductionParameters productionParameters;

	protected final Map<String, String> mapPreviousCode = new HashMap<String, String>();
	protected final Map<String, Integer> mapPreviousCounter = new HashMap<String, Integer>();

	protected CameraDuplicatedAlertTaskModel model;

	protected final Object lock = new Object();

	@Override
	protected boolean isAlertPresent() {
		synchronized (lock) {
			for (Entry<String, Integer> entry : mapPreviousCounter.entrySet()) {
				if (entry.getValue() >= getModel().getThreshold()) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.DUPLICATED_CODE);
	}

	@Override
	protected boolean isEnabled() {
		if (!productionParameters.getProductionMode().isWithSicpaData()) {
			return false;
		}
		return getModel().getThreshold() > 0 && getModel().isEnabled();
	}

	protected String getPreviousCode(String camera) {
		return mapPreviousCode.get(camera);
	}

	@Subscribe
	public void receiveCameraCode(final CameraGoodCodeEvent cameraEvent) {
		synchronized (lock) {
			String cameraName = cameraEvent.getSource().getName();
			String previousCode = getPreviousCode(cameraName);
			String currentCode = cameraEvent.getCode().getStringCode();

			if (getModel().getThreshold() > 0 && previousCode != null && previousCode.equals(currentCode)) {
				int counter = mapPreviousCounter.get(cameraName);
				counter++;
				mapPreviousCounter.put(cameraName, counter);
				checkForMessage();
			} else {
				mapPreviousCounter.put(cameraName, 1);
			}
			mapPreviousCode.put(cameraName, currentCode);
		}
	}

	@Override
	public void reset() {
		synchronized (lock) {
			mapPreviousCode.clear();
			mapPreviousCounter.clear();
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public String getAlertName() {
		return "Duplicated code alert";
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setModel(CameraDuplicatedAlertTaskModel model) {
		this.model = model;
	}

	public CameraDuplicatedAlertTaskModel getModel() {
		return model;
	}
}
