package com.sicpa.standard.sasscl.devices.camera.d900.alert;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledBadCountAlertTask;
import com.sicpa.standard.sasscl.devices.d900.D900CameraCodeEvent;
import com.sicpa.standard.sasscl.devices.d900.D900CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.function.Function;

/**
 * implementation of an alert task for the camera<br/>
 * send an alert when too many bad codes are received from the camera
 *
 * @author DIelsch
 */
public abstract class D900AbstractCameraCountAlertTask extends AbstractScheduledBadCountAlertTask {

	private ProductionParameters productionParameters;
	private CameraCountAlertTaskModel model;

	private Function<D900CameraCodeEvent, Boolean> codeValidator = (evt) -> isValidCodeDefaultImpl(evt);

	public D900AbstractCameraCountAlertTask() {
		super();
	}


	@Subscribe
	public void receiveCameraCode(D900CameraCodeEvent evt) {
		dispatchEvent(evt);
	}

	private void dispatchEvent(D900CameraCodeEvent evt) {
		if (isValidCode(evt)) {
			increaseGood();
		} else {
			increaseBad();
		}
	}

	public void setCodeValidator(Function<D900CameraCodeEvent, Boolean> isValidCode) {
		this.codeValidator = isValidCode;
	}

	private boolean isValidCode(D900CameraCodeEvent evt) {
		return codeValidator.apply(evt);
	}

	protected boolean isValidCodeDefaultImpl(D900CameraCodeEvent evt) {
         /* if the  CameraBadCodeEvent is mark as blob detected then
          * we can consider as valid */
		return evt instanceof D900CameraGoodCodeEvent;
	}

	@Override
	protected boolean isEnabledDefaultImpl() {
		if (!productionParameters.getProductionMode().isWithSicpaData()) {
			return false;
		}
		return model.isEnabled();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public long getDelay() {
		return getModel().getDelay();
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
