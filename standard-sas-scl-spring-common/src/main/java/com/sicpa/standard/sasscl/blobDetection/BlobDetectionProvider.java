package com.sicpa.standard.sasscl.blobDetection;


/**
 * Implementations of this interface should provide the current production state of the blob detection mechanism. As a
 * example of an implementation see the class {@link BlobDetectionSKUProvider}.
 */
public interface BlobDetectionProvider {

    /**
     * @return the current production blob detection state as an instance of {@link BlobDetection}.
     */
    BlobDetection getProductionBlobDetectionState();


    /**
     * @return <code>true</code> if the blob detection mechanism is enable globaly in the client.
     */
    boolean isBlobDetectionEnableGlobally();

}
