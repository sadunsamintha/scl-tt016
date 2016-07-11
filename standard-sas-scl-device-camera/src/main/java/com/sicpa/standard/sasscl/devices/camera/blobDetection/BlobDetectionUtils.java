package com.sicpa.standard.sasscl.devices.camera.blobDetection;

import com.sicpa.standard.sasscl.devices.camera.CameraConstants;
import com.sicpa.standard.sasscl.model.Code;

public class BlobDetectionUtils {

    private BlobDetectionProvider blobDetectionProvider;

    public BlobDetectionUtils(BlobDetectionProvider blobDetectionProvider) {
        this.blobDetectionProvider = blobDetectionProvider;
    }

    /**
     * @param code The {@link Code} return by the camera.
     * @return returns <code>true</code> if the  content of the {@link Code} can be interpreted as the camera has detected presence of blobs/ink.
     */
    public boolean isBlobDetected(final Code code) {
        final boolean isBlobEnable = blobDetectionProvider.getProductionBlobDetectionState().isBlobDetectionEnable();
        return isBlobEnable && CameraConstants.getCameraBlobErrorCode().equals(code);
    }


}
