package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;
import junit.framework.Assert;
import org.junit.Test;

public class CameraConstantsTest {

    @Test
    public void cameraBlobErrorCode() {
        Code cameraBlobError = CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals("100", cameraBlobError.getStringCode());
    }

    @Test
    public void cameraErrorCode() {
        Code cameraError = CameraConstants.getCameraErrorCode();
        Assert.assertEquals("0", cameraError.getStringCode());
    }

    @Test
    public void setCameraBlobErrorCode() {
        CameraConstants.setCameraBlobErrorCode("300");
        Code cameraBlobError = CameraConstants.getCameraBlobErrorCode();
        Assert.assertEquals("300", cameraBlobError.getStringCode());
    }

    @Test
    public void setcameraErrorCode() {
        CameraConstants.setCameraErrorCode("-1");
        Code cameraError = CameraConstants.getCameraErrorCode();
        Assert.assertEquals("-1", cameraError.getStringCode());
    }

}
