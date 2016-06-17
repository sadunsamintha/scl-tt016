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
 * implementation of an alert task for the camera<br/>
 * send an alert when too many bad codes are received from the camera
 *
 * @author DIelsch
 */
public class CameraCountAlertTask extends AbstractScheduledBadCountAlertTask {

    private ProductionParameters productionParameters;
    private CameraCountAlertTaskModel model;

    private BlobDetectionUtils blobDetectionUtils;

    private Function<CameraCodeEvent, Boolean> codeValidator = (evt) -> isValidCodeDefaultImpl(evt);

    public CameraCountAlertTask() {
        super();
    }

    @Override
    protected MessageEvent getAlertMessage() {
        return new MessageEvent(MessageEventKey.Alert.TOO_MUCH_CAMERA_ERROR);
    }

    @Subscribe
    public void receiveCameraCode(CameraCodeEvent evt) {
        dispatchEvent(evt);
    }

    private void dispatchEvent(CameraCodeEvent evt) {
        if (isValidCode(evt)) {
            increaseGood();
        } else {
            increaseBad();
        }
    }

    public void setCodeValidator(Function<CameraCodeEvent, Boolean> isValidCode) {
        this.codeValidator = isValidCode;
    }

    private boolean isValidCode(CameraCodeEvent evt) {
        return codeValidator.apply(evt);
    }

    protected boolean isValidCodeDefaultImpl(CameraCodeEvent evt) {
         /* if the  CameraBadCodeEvent is mark as blob detected then
          * we can consider as valid */
        return evt instanceof CameraGoodCodeEvent || blobDetectionUtils.isBlobDetected(evt.getCode());
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
    public String getAlertName() {
        return "Too many bad codes alert";
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

    public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
        this.blobDetectionUtils = blobDetectionUtils;
    }


}
