package com.sicpa.standard.sasscl.devices.camera.blobDetection;


import com.sicpa.standard.sasscl.blobDetection.BlobDetectionMode;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import junit.framework.Assert;
import org.junit.Test;

public class BlobDetectionStateSKUProviderTest {

    @Test
    public void blobDetectionAlwaysEnable() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertTrue(isBlobDetectionEnable);
    }

    @Test
    public void blobDetectionNotEnabledInProduction() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.STANDARD);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertFalse(isBlobDetectionEnable);
    }

    @Test
    public void blobDetectionEnabledInProduction() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKUWithBlobDetectionEnable()), BlobDetectionMode.STANDARD);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertTrue(isBlobDetectionEnable);
    }

    @Test
    public void isBlobDetectionEnableStandard() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.STANDARD);
        Assert.assertTrue(blobDetectionProvider.isBlobDetectionEnable());
    }

    @Test
    public void isBlobDetectionEnableAlways() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS);
        Assert.assertTrue(blobDetectionProvider.isBlobDetectionEnable());
    }

    @Test
    public void isBlobDetectionEnableNone() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.NONE);
        Assert.assertFalse(blobDetectionProvider.isBlobDetectionEnable());
    }

    private ProductionParameters createProductionParameters(SKU sku) {
        ProductionParameters productionParameters = new ProductionParameters();
        productionParameters.setSku(sku);
        return productionParameters;
    }


    private class SKUWithBlobDetectionEnable extends SKU {
        public boolean isBlobDetectionEnable() {
            return true;
        }

    }

}