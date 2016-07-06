package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;
import junit.framework.Assert;
import org.junit.Test;


public class CameraConstantsTest {


    private static final String BLOB_ERROR_CODE_300 = "300";

    private static final String BLOB_ERROR_CODE_100 = "100";

    private static final String CAMERA_ERROR_CODE_0 = "0";

    private static final String CAMERA_ERROR_CODE_1 = "-1";


    @Test
    public void cameraBlobErrorCode() {
        Code cameraBlobError = CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals(BLOB_ERROR_CODE_100, cameraBlobError.getStringCode());
    }

    @Test
    public void cameraErrorCode() {
        Code cameraError = CameraConstants.getCameraErrorCode();
        Assert.assertEquals(CAMERA_ERROR_CODE_0, cameraError.getStringCode());
    }

    @Test
    public void setCameraBlobErrorCode() {
        CameraConstants.setCameraBlobErrorCode(BLOB_ERROR_CODE_300);
        Code cameraBlobError = CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals(BLOB_ERROR_CODE_300, cameraBlobError.getStringCode());
        CameraConstants.setCameraBlobErrorCode(BLOB_ERROR_CODE_100);
    }

    @Test
    public void setcameraErrorCode() {
        CameraConstants.setCameraErrorCode(CAMERA_ERROR_CODE_1);
        Code cameraError = CameraConstants.getCameraErrorCode();
        Assert.assertEquals(CAMERA_ERROR_CODE_1, cameraError.getStringCode());
        CameraConstants.setCameraErrorCode(CAMERA_ERROR_CODE_0);
    }

}
