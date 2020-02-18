package com.sicpa.standard.sasscl.devices.camera.blobDetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlobDetectionEnableLogger implements IBlobDetectionEnableLogger {

    private BlobDetectionProvider blobDetectionProvider;

    private static final Logger logger = LoggerFactory.getLogger(BlobDetectionEnableLogger.class);

    public void logBlobDetectionEnable(){

        if (blobDetectionProvider != null && blobDetectionProvider.isBlobDetectionEnable()) {

            logger.info("Blob detection enable");
        }
    }

    public void setBlobDetectionProvider(BlobDetectionProvider blobDetectionProvider){

        this.blobDetectionProvider = blobDetectionProvider;
    }
}
