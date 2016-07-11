package com.sicpa.standard.sasscl.blobDetection;


/**
 * Use this interface to define if the blob/ink detection mechanism is enable or not.
 */
public interface BlobDetectionState {

    /**
     * @return <code>true</code> if the blob detection mechanism is enable otherwise returns <code>false</code>.
     */
    default boolean isBlobDetectionEnable() {
        return false;
    }

}
