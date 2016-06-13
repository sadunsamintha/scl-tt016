package com.sicpa.standard.sasscl.blobDetection;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import junit.framework.Assert;
import org.junit.Test;

public class BlobDetectionUtilsTest {



    @Test
    public void blobDetected() {
        BlobDetectionUtils utils = new BlobDetectionUtils();
        Code code = new Code();
        code.setStringCode("100");
        boolean blobDetected = utils.isBlobDetected(code);
        Assert.assertTrue(blobDetected);
    }

    @Test
    public void blobNotDetected() {
        BlobDetectionUtils utils = new BlobDetectionUtils();
        Code code = new Code();
        code.setStringCode("0");
        boolean blobDetected = utils.isBlobDetected(code);
        Assert.assertFalse(blobDetected);
    }



    private class SKUWithBlobDetectionEnable extends SKU {

        @Override
        public boolean isBlobDetectionEnable() {
            return true;
        }
    }

}
