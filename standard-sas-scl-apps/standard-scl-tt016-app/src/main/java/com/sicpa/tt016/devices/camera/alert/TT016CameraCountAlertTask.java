package com.sicpa.tt016.devices.camera.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class TT016CameraCountAlertTask extends CameraCountAlertTask {

    @Override
    protected MessageEvent getAlertMessage() {
        return new MessageEvent(MessageEventKey.Alert.TOO_MUCH_CAMERA_WARNING);
    }
}
