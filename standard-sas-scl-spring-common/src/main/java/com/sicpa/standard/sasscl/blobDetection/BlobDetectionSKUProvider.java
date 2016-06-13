package com.sicpa.standard.sasscl.blobDetection;

import com.sicpa.standard.sasscl.model.ProductionParameters;


public class BlobDetectionSKUProvider implements BlobDetectionProvider {

    private ProductionParameters productionParameters;

    private BlobDetectionMode blobDetectionMode ;


    public BlobDetectionSKUProvider(ProductionParameters productionParameters, BlobDetectionMode blobDetectionMode) {
        this.productionParameters = productionParameters;
        this.blobDetectionMode = blobDetectionMode;
    }

    @Override
    public BlobDetection getProductionBlobDetectionState() {
        switch (blobDetectionMode) {
            case NONE:
                return createBlobDetectionDisable();
            case ALWAYS:
                return createBlobDetectionAlwaysEnable();
            case STANDARD:
                return productionParameters.getSku();
        }
        throw new IllegalStateException("Blob detection mode can only have 3 modes : NONE; ALWAYS and STANDARD");
    }

    private BlobDetection createBlobDetectionAlwaysEnable() {
        return new BlobDetection() {
            @Override
            public boolean isBlobDetectionEnable() {
                return true;
            }
        };
    }


    private BlobDetection createBlobDetectionDisable() {
        return new BlobDetection() {
            @Override
            public boolean isBlobDetectionEnable() {
                return false;
            }
        };
    }
}
