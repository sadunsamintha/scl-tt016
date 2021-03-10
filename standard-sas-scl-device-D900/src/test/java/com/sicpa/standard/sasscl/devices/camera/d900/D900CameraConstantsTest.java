package com.sicpa.standard.sasscl.devices.camera.d900;

import com.sicpa.standard.sasscl.model.Code;
import junit.framework.Assert;
import org.junit.Test;


public class D900CameraConstantsTest {


    private static final String BLOB_ERROR_CODE_300 = "300";

    private static final String BLOB_ERROR_CODE_100 = "100";

    private static final String CAMERA_ERROR_CODE_0 = "0";

    private static final String CAMERA_ERROR_CODE_1 = "-1";


    @Test
    public void cameraBlobErrorCode() {
        Code cameraBlobError = D900CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals(BLOB_ERROR_CODE_100, cameraBlobError.getStringCode());
    }

    @Test
    public void cameraErrorCode() {
        Code cameraError = D900CameraConstants.getCameraErrorCode();
        Assert.assertEquals(CAMERA_ERROR_CODE_0, cameraError.getStringCode());
    }

    @Test
    public void setCameraBlobErrorCode() {
        D900CameraConstants.setCameraBlobErrorCode(BLOB_ERROR_CODE_300);
        Code cameraBlobError = D900CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals(BLOB_ERROR_CODE_300, cameraBlobError.getStringCode());
        D900CameraConstants.setCameraBlobErrorCode(BLOB_ERROR_CODE_100);
    }

    @Test
    public void setcameraErrorCode() {
        D900CameraConstants.setCameraErrorCode(CAMERA_ERROR_CODE_1);
        Code cameraError = D900CameraConstants.getCameraErrorCode();
        Assert.assertEquals(CAMERA_ERROR_CODE_1, cameraError.getStringCode());
        D900CameraConstants.setCameraErrorCode(CAMERA_ERROR_CODE_0);
    }

}
