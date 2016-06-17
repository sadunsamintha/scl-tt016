package com.sicpa.standard.sasscl.blobDetection;


public enum BlobDetectionMode {

    /**
     * Blob detection mechanism is disable
     */
    NONE,

    /**
     * Blob detection mechanism is enable and is active based on the production selection given by the  BlobDetectionProvider.
     */
    STANDARD,

    /**
     * Blob detection mechanism is enable and always active.
     */
    ALWAYS;
}
