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

	private ProductionParameters productionParameters;
	private CameraDuplicatedAlertTaskModel model;

	private final Map<String, String> previousCodeByLine = new HashMap<>();
	private final Map<String, Integer> previousCounterByLine = new HashMap<>();
	private final Object lock = new Object();

	@Override
	protected boolean isAlertPresent() {
		synchronized (lock) {
			for (Entry<String, Integer> entry : previousCounterByLine.entrySet()) {
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
	protected boolean isEnabledDefaultImpl() {
		if (!productionParameters.getProductionMode().isWithSicpaData()) {
			return false;
		}
		return getModel().getThreshold() > 0 && getModel().isEnabled();
	}

	private String getPreviousCode(String camera) {
		return previousCodeByLine.get(camera);
	}

	@Subscribe
	public void receiveCameraCode(CameraGoodCodeEvent cameraEvent) {
		String cameraName = cameraEvent.getCode().getSource();
		String currentCode = cameraEvent.getCode().getStringCode();
		synchronized (lock) {
			if (isDuplicate(cameraEvent)) {
				increaseCounter(cameraName);
				checkForMessage();
			} else {
				resetCounter(cameraName);
			}
			previousCodeByLine.put(cameraName, currentCode);
		}
	}

	private void resetCounter(String cameraName) {
		previousCounterByLine.put(cameraName, 1);
	}

	private void increaseCounter(String cameraName) {
		int counter = previousCounterByLine.get(cameraName);
		counter++;
		previousCounterByLine.put(cameraName, counter);
	}

	private boolean isDuplicate(CameraGoodCodeEvent cameraEvent) {
		String cameraName = cameraEvent.getCode().getSource();
		String previousCode = getPreviousCode(cameraName);
		String currentCode = cameraEvent.getCode().getStringCode();
		return getModel().getThreshold() > 0 && previousCode != null && previousCode.equals(currentCode);
	}

	@Override
	public void reset() {
		synchronized (lock) {
			previousCodeByLine.clear();
			previousCounterByLine.clear();
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
