package com.sicpa.standard.sasscl.devices.printer.xcode;


import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionMode;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionSKUProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SicpaDataAndBlobExCodeBehaviorTest {
    @Test
    public void createExCodes() {
        SicpaDataAndBlobExCodeBehavior extendedCodeFactory = createExtendedCodeFactory();
        List<String> codes = Arrays.asList("0996908468");
        List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        // verify we have created 2 blocks
        ExtendedCode extendedCode = extendedCodes.get(0);
        Assert.assertEquals(2, extendedCode.getNumberOfBlock());


        //verify the datamatrix block 0
        ExtendedCode.Block block0 = extendedCode.getBlock(0);
        Assert.assertTrue(block0.isDatamatrix());
        Assert.assertEquals("0996908468", block0.getData());

        //verify the blob block 1
        ExtendedCode.Block block1 = extendedCode.getBlock(1);
        Assert.assertTrue(block1.isBlob());
        Assert.assertEquals(RelativePosition.ABOVE, block1.getRelativePosition());

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrongExCodes() {
        SicpaDataAndBlobExCodeBehavior extendedCodeFactory = createExtendedCodeFactory();
        extendedCodeFactory.createExCodes(null);
    }


    private SicpaDataAndBlobExCodeBehavior createExtendedCodeFactory() {
        SicpaDataAndBlobExCodeBehavior extendedCodeFactory = new SicpaDataAndBlobExCodeBehavior();
        extendedCodeFactory.setDmFormat(ModelDataMatrixFormat.DM_8x18);
        extendedCodeFactory.setDmEncoding(ModelDataMatrixEncoding.ASCII);
        extendedCodeFactory.setBlobPosition(RelativePosition.ABOVE);
        BlobDetectionUtils blobUtils = new BlobDetectionUtils(createBlobDetectionProvider());
        extendedCodeFactory.setBlobUtils(blobUtils);
        return extendedCodeFactory;
    }

    private BlobDetectionProvider createBlobDetectionProvider() {
        return new BlobDetectionSKUProvider(new ProductionParameters(), BlobDetectionMode.ALWAYS, true);
    }


}
