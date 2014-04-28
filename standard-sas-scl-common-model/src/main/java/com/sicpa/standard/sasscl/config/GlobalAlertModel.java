package com.sicpa.standard.sasscl.config;

import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraDuplicatedAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraIddleAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.model.PlcActivationCounterCheckModel;

public class GlobalAlertModel {

	protected CameraCountAlertTaskModel cameraCountModel;

	protected CameraDuplicatedAlertTaskModel duplicatedCodeModel;
	protected CameraIddleAlertTaskModel cameraIddleModel;

	protected PlcActivationCounterCheckModel plcActivationCrossCheckModel;

	public CameraCountAlertTaskModel getCameraCountModel() {
		return cameraCountModel;
	}

	public void setCameraCountModel(CameraCountAlertTaskModel cameraCountModel) {
		this.cameraCountModel = cameraCountModel;
	}

	public CameraDuplicatedAlertTaskModel getDuplicatedCodeModel() {
		return duplicatedCodeModel;
	}

	public void setDuplicatedCodeModel(CameraDuplicatedAlertTaskModel duplicatedCodeModel) {
		this.duplicatedCodeModel = duplicatedCodeModel;
	}

	public CameraIddleAlertTaskModel getCameraIddleModel() {
		return cameraIddleModel;
	}

	public void setCameraIddleModel(CameraIddleAlertTaskModel cameraIddleModel) {
		this.cameraIddleModel = cameraIddleModel;
	}

	public PlcActivationCounterCheckModel getPlcActivationCrossCheckModel() {
		return plcActivationCrossCheckModel;
	}

	public void setPlcActivationCrossCheckModel(PlcActivationCounterCheckModel plcActivationCrossCheckModel) {
		this.plcActivationCrossCheckModel = plcActivationCrossCheckModel;
	}
}
