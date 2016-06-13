package com.sicpa.standard.sasscl.blobDetection;


import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import junit.framework.Assert;
import org.junit.Test;

public class BlobDetectionSKUProviderTest {



    @Test
    public void blobDetectionEnabledInProduction() {
        BlobDetectionUtils utils = new BlobDetectionUtils();
        utils.setBlobDetectionAlwaysEnable(false);
        utils.setBlobPrinterPatternEnable(false);
        utils.setBlobDetectionProvider(createBlobDetectionProvider(true));

        boolean enableInProd = utils.isBlobDetectionEnabledInProduction();
        Assert.assertTrue(enableInProd);
    }

    @Test
    public void blobDetectionNotEnabledInProduction() {
        BlobDetectionUtils utils = new BlobDetectionUtils();
        utils.setBlobDetectionAlwaysEnable(false);
        utils.setBlobPrinterPatternEnable(false);
        utils.setBlobDetectionProvider(createBlobDetectionProvider(false));

        boolean enableInProd = utils.isBlobDetectionEnabledInProduction();
        Assert.assertFalse(enableInProd);
    }

    @Test
    public void blobDetectionAlwaysEnable() {
        BlobDetectionUtils utils = new BlobDetectionUtils();
        utils.setBlobDetectionAlwaysEnable(true);
        utils.setBlobPrinterPatternEnable(false);
        utils.setBlobDetectionProvider(createBlobDetectionProvider(false));

        boolean enableInProd = utils.isBlobDetectionEnabledInProduction();
        Assert.assertTrue(enableInProd);
    }


    private BlobDetectionProvider createBlobDetectionProvider(BlobDetectionMode blobDetectionMode) {
        ProductionParameters prodParameters = new ProductionParameters();
        SKU sku = blobDetectionEnable ? createSKUWithBlobDetectionEnable() : new SKU();
        prodParameters.setSku(sku);
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(prodParameters, blobDetectionMode);
        return blobDetectionProvider;
    }

    private BlobDetection createSKUWithBlobDetectionEnable() {
        return new BlobDetection() {
            @Override
            public boolean isBlobDetectionEnable() {
                return false;
            }
        };
    }


}
