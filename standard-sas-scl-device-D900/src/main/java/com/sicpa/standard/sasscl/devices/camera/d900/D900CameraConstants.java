package com.sicpa.standard.sasscl.devices.camera.d900;

import com.sicpa.standard.sasscl.model.Code;

public class D900CameraConstants {

    private D900CameraConstants() {
        //prevent instantiation
    }

    /* Error codes from camera job specification */

    private static Code cameraBlobErrorCode = new Code("100");

    private static Code cameraErrorCode = new Code("0");


    public static Code getCameraBlobErrorCode() {
        return cameraBlobErrorCode;
    }

    public static Code getCameraErrorCode() {
        return cameraErrorCode;
    }

    public static void setCameraBlobErrorCode(String cameraBlobErrorCodeStr) {
        D900CameraConstants.cameraBlobErrorCode.setStringCode(cameraBlobErrorCodeStr);
    }

    public static void setCameraErrorCode(String cameraErrorCodeStr) {
        D900CameraConstants.cameraErrorCode.setStringCode(cameraErrorCodeStr);
    }

}