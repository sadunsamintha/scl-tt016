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
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS,true);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertTrue(isBlobDetectionEnable);
    }

    @Test
    public void blobDetectionNotEnabledInProduction() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.STANDARD, true);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertFalse(isBlobDetectionEnable);
    }

    @Test
    public void blobDetectionEnabledInProduction() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKUWithBlobDetectionEnable()), BlobDetectionMode.STANDARD, true);
        BlobDetectionState blobDetectionState = blobDetectionProvider.getProductionBlobDetectionState();
        boolean isBlobDetectionEnable = blobDetectionState.isBlobDetectionEnable();
        Assert.assertTrue(isBlobDetectionEnable);
    }

    @Test
    public void isBlobDetectionEnableStandard() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.STANDARD, true);
        Assert.assertTrue(blobDetectionProvider.isBlobDetectionEnable());
    }

    @Test
    public void isBlobDetectionEnableAlways() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS, true);
        Assert.assertTrue(blobDetectionProvider.isBlobDetectionEnable());
    }

    @Test
    public void isBlobDetectionEnableNone() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.NONE, false);
        Assert.assertFalse(blobDetectionProvider.isBlobDetectionEnable());
    }

    @Test
    public void BlobPatternPrinted() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS, true);
        Assert.assertTrue(blobDetectionProvider.isBlobPatternPrinted());
    }

    @Test
    public void BlobPatternNonPrinted() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS, false);
        Assert.assertFalse(blobDetectionProvider.isBlobPatternPrinted());
    }

    @Test
    public void BlobPatternPrintedButBlobDetectionDisable() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.NONE, true);
        Assert.assertFalse(blobDetectionProvider.isBlobPatternPrinted());
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