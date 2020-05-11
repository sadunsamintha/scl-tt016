package com.sicpa.tt065.devices.camera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;

public class TT065BlobDetectionEnableLogger implements TT065IBlobDetectionEnableLogger {

    private BlobDetectionProvider blobDetectionProvider;

    private static final Logger logger = LoggerFactory.getLogger(TT065BlobDetectionEnableLogger.class);

    public void logBlobDetectionEnable(){

        if (blobDetectionProvider != null && blobDetectionProvider.isBlobDetectionEnable()) {

            logger.info("Blob detection enable");
        }
    }

    public void setBlobDetectionProvider(BlobDetectionProvider blobDetectionProvider){

        this.blobDetectionProvider = blobDetectionProvider;
    }
}
