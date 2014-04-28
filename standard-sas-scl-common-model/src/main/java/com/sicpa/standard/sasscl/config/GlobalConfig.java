package com.sicpa.standard.sasscl.config;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraDuplicatedAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraIddleAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.PlcActivationCounterCheckModel;

public class GlobalConfig extends GlobalBean {

	private static final long serialVersionUID = -5282385352656130481L;

	public GlobalConfig(final String file) {
		setDefaultValues();
		ConfigUtils.loadCopySave(this, file);
	}

	public GlobalConfig() {
		setDefaultValues();
	}

	public void setDefaultValues() {
		setLanguage("en");
		setCleanUpSendDataThreshold_day(30);
		setProductionSendBatchSize(30);
		setDisplaySimulatorGui(false);
		setProductionDataSerializationErrorThreshold(3);
		setRemoteServerTimeoutCall_sec(60);
		setSubsystemId(-1);

		GlobalAlertModel alert = new GlobalAlertModel();
		alert.setCameraCountModel(new CameraCountAlertTaskModel());
		alert.getCameraCountModel().setDelayInSec(5000);
		alert.getCameraCountModel().setMaxUnreadCount(5);
		alert.getCameraCountModel().setSampleSize(100);
		alert.setDuplicatedCodeModel(new CameraDuplicatedAlertTaskModel());
		alert.getDuplicatedCodeModel().setThreshold(5);
		alert.setCameraIddleModel(new CameraIddleAlertTaskModel());
		alert.getCameraIddleModel().setMaxInactiveTimeInSec(60);
		alert.setPlcActivationCrossCheckModel(new PlcActivationCounterCheckModel());
		alert.getPlcActivationCrossCheckModel().setMaxDelta(10);
		setAlertModel(alert);
	}
}
