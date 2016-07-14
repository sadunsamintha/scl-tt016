package com.sicpa.standard.sasscl.devices.camera.blobDetection;


import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;

/**
 * Implementations of this interface should provide the global and current production state of the blob detection
 * mechanism . As an example see the class {@link BlobDetectionSKUProvider} implementation.
 */
public interface BlobDetectionProvider {

    /**
     * @return the current production blob detection state as an instance of {@link BlobDetectionState}.
     */
    BlobDetectionState getProductionBlobDetectionState();


    /**
     * @return <code>true</code> if the blob detection mechanism is enable in the client otherwise
     * returns <code>false</code>.
     */
    boolean isBlobDetectionEnable();

}
