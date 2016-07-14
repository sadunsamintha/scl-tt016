package com.sicpa.standard.sasscl.devices.camera.blobDetection;

import com.sicpa.standard.sasscl.blobDetection.BlobDetectionMode;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.apache.commons.lang3.Validate;


public class BlobDetectionSKUProvider implements BlobDetectionProvider {

    private ProductionParameters productionParameters;

    private BlobDetectionMode blobDetectionMode ;


    /**
     * @throws IllegalArgumentException if any of the given parameters are <code>null</code>.
     */
    public BlobDetectionSKUProvider(ProductionParameters productionParameters, BlobDetectionMode blobDetectionMode) {
        Validate.notNull(productionParameters);
        Validate.notNull(blobDetectionMode);

        this.productionParameters = productionParameters;
        this.blobDetectionMode = blobDetectionMode;
    }

    @Override
    public BlobDetectionState getProductionBlobDetectionState() {
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

    @Override
    public boolean isBlobDetectionEnable() {
        return blobDetectionMode != BlobDetectionMode.NONE;
    }

    private BlobDetectionState createBlobDetectionAlwaysEnable() {
        return new BlobDetectionState() {
            @Override
            public boolean isBlobDetectionEnable() {
                return true;
            }
        };
    }


    private BlobDetectionState createBlobDetectionDisable() {
        return new BlobDetectionState() {
            @Override
            public boolean isBlobDetectionEnable() {
                return false;
            }
        };
    }
}
