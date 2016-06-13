package com.sicpa.standard.sasscl.blobDetection;

import com.sicpa.standard.sasscl.devices.camera.CameraConstants;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class BlobDetectionUtils {

    private boolean blobDetectionAlwaysEnable;

    private BlobDetectionProvider blobDetectionProvider;

    /**
     * @param code The {@link Code} return by the camera.
     * @return returns <code>true</code> if the  content of the {@link Code} can be interpreted as the camera has detected presence of blobs/ink.
     */
    public boolean isBlobDetected(final Code code) {
        return CameraConstants.getCameraBlobErrorCode().equals(code);
    }

    public void setBlobDetectionProvider(BlobDetectionProvider blobDetectionProvider) {
        this.blobDetectionProvider = blobDetectionProvider;
    }


}
