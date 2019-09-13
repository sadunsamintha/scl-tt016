package com.sicpa.tt080.printer;


import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TT080ExtendedCodeFactoryTest {


    @Test
    public void createExCodes() {
        TT080ExtendedCodeFactory extendedCodeFactory = createExtendedCodeFactory();
        List<String> codes = Arrays.asList("0996908468>-<093G001YG");
        List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        // verify we have created 4 blocks
        ExtendedCode extendedCode = extendedCodes.get(0);
        Assert.assertEquals(4, extendedCode.getNumberOfBlock());

        //verify hrd block 0
        ExtendedCode.Block block0 = extendedCode.getBlock(0);
        Assert.assertTrue(block0.isAsciiText());
        Assert.assertEquals(RelativePosition.RIGHT, block0.getRelativePosition());
        Assert.assertEquals("093", block0.getData());

        //verify hrd block 1
        ExtendedCode.Block block1 = extendedCode.getBlock(1);
        Assert.assertTrue(block1.isAsciiText());
        Assert.assertEquals(RelativePosition.BELOW, block1.getRelativePosition());
        Assert.assertEquals("G001YG", block1.getData());

        //verify the datamatrix block 2
        ExtendedCode.Block block2 = extendedCode.getBlock(2);
        Assert.assertTrue(block2.isDatamatrix());
        Assert.assertEquals("0996908468", block2.getData());

        //verify the blob block 3
        ExtendedCode.Block block3 = extendedCode.getBlock(3);
        Assert.assertTrue(block3.isBlob());
        Assert.assertEquals(RelativePosition.ABOVE, block3.getRelativePosition());

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrongExCodes() {
        TT080ExtendedCodeFactory extendedCodeFactory = createExtendedCodeFactory();
        List<String> codes = Arrays.asList("0996908468-093G001YG");
        extendedCodeFactory.createExCodes(codes);

    }


    private TT080ExtendedCodeFactory createExtendedCodeFactory() {
        TT080ExtendedCodeFactory extendedCodeFactory = new TT080ExtendedCodeFactory();
        extendedCodeFactory.setDmFormat(ModelDataMatrixFormat.DM_8x18);
        extendedCodeFactory.setDmEncoding(ModelDataMatrixEncoding.ASCII);
        extendedCodeFactory.setBlobPosition(RelativePosition.ABOVE);
        extendedCodeFactory.setText1Length(3);
        extendedCodeFactory.setText1Position(RelativePosition.RIGHT);
        extendedCodeFactory.setText2Length(6);
        extendedCodeFactory.setText2Position(RelativePosition.BELOW);

        BlobDetectionProvider provider = new BlobDetectionProvider() {
            @Override
            public BlobDetectionState getProductionBlobDetectionState() {
                return new BlobDetectionState() {
                    @Override
                    public boolean isBlobDetectionEnable() {
                        return true;
                    }
                };
            }

            @Override
            public boolean isBlobDetectionEnable() {
                return true;
            }

            @Override
            public boolean isBlobPatternPrinted() {
                return true;
            }
        };

        BlobDetectionUtils blobUtils = new BlobDetectionUtils(provider);
        extendedCodeFactory.setBlobUtils(blobUtils);

        return extendedCodeFactory;
    }

}