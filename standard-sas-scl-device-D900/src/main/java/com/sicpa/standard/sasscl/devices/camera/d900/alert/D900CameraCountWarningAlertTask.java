package com.sicpa.standard.sasscl.devices.camera.d900.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

/**
 * implementation of a camera alert task.<br/>
 * Send a warning alert when too many bad codes are received from the camera given a configurable threshold.
 */
public class D900CameraCountWarningAlertTask extends D900AbstractCameraCountAlertTask {

    private static final String ALERT_NAME = "Too many bad codes warning alert";

    @Override
    protected MessageEvent getAlertMessage() {
        return new MessageEvent(MessageEventKey.Alert.TOO_MANY_VIDI_WARNING);
    }

    @Override
    public String getAlertName() {
        return ALERT_NAME;
    }

}
