package com.sicpa.standard.sasscl.devices.camera.blobDetection;

import com.sicpa.standard.sasscl.blobDetection.BlobDetectionMode;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionSKUProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import junit.framework.Assert;
import org.junit.Test;

public class BlobDetectionUtilsTest {


    @Test
    public void blobDetected() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS);
        BlobDetectionUtils blobDetectionUtils = new BlobDetectionUtils(blobDetectionProvider);


        Code code = new Code();
        code.setStringCode("100");
        boolean blobDetected = blobDetectionUtils.isBlobDetected(code);
        Assert.assertTrue(blobDetected);
    }

    @Test
    public void blobNotDetected() {
        BlobDetectionProvider blobDetectionProvider = new BlobDetectionSKUProvider(createProductionParameters(new SKU()), BlobDetectionMode.ALWAYS);
        BlobDetectionUtils blobDetectionUtils = new BlobDetectionUtils(blobDetectionProvider);


        Code code = new Code();
        code.setStringCode("0");
        boolean blobDetected = blobDetectionUtils.isBlobDetected(code);
        Assert.assertFalse(blobDetected);
    }


    private ProductionParameters createProductionParameters(SKU sku) {
        ProductionParameters productionParameters = new ProductionParameters();
        productionParameters.setSku(sku);
        return productionParameters;
    }


}
