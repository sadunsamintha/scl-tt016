package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public class CameraConstants {

    private CameraConstants() {
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
        CameraConstants.cameraBlobErrorCode.setStringCode(cameraBlobErrorCodeStr);
    }

    public static void setCameraErrorCode(String cameraErrorCodeStr) {
        CameraConstants.cameraErrorCode.setStringCode(cameraErrorCodeStr);
    }

}