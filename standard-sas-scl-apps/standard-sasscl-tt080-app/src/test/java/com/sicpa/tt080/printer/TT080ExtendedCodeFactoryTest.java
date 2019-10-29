package com.sicpa.tt080.printer;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TT080ExtendedCodeFactoryTest {

    private TT080ExtendedCodeFactory extendedCodeFactory;

    @Before
    public void setup(){
        extendedCodeFactory = createExtendedCodeFactory();
    }

    @Test
    public void createExCodes_Returns_CorrectNumberOfBlocks() {
        //Given
        final List<String> codes = Collections.singletonList("76498801412>-<8R0P016H");

        //When
        final List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        //Then
        final ExtendedCode extendedCode = extendedCodes.get(0);
        assertThat(extendedCode.getNumberOfBlock(), is(4));
    }

    @Test
    public void createExCodes_Returns_CorrectHRCBlock0() {
        //Given
        final List<String> codes = Collections.singletonList("76498801412>-<8R0P016H");

        //When
        final List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        //Then
        final ExtendedCode extendedCode = extendedCodes.get(0);
        final ExtendedCode.Block hrcBlock0 = extendedCode.getBlock(0);
        assertThat(hrcBlock0.isAsciiText(), is(true));
        assertThat(hrcBlock0.getRelativePosition(), is(RelativePosition.RIGHT));
        final String blockData = hrcBlock0.getData().toString();
        assertThat(blockData, is("8R0"));
    }

    @Test
    public void createExCodes_Returns_CorrectSHRCBlock1() {
        //Given
        final List<String> codes = Collections.singletonList("76498801412>-<431X9861");

        //When
        final List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        //Then
        final ExtendedCode.Block hrckBlock1 = extendedCodes.get(0).getBlock(1);
        Assert.assertThat(hrckBlock1.isAsciiText(), is(true));
        Assert.assertThat(hrckBlock1.getRelativePosition(), is(RelativePosition.BELOW));
        Assert.assertThat(hrckBlock1.getData().toString(), is("X9861"));
    }

    @Test
    public void createExCodes_Returns_CorrectSDMXBlock() {
        //Given
        final List<String> codes = Collections.singletonList("76498801412>-<431X9861");

        //When
        final List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        //Then
        final ExtendedCode.Block DMXBlock = extendedCodes.get(0).getBlock(2);
        Assert.assertThat(DMXBlock.isDatamatrix(), is(true));
        Assert.assertThat(DMXBlock.getData().toString(), is("76498801412"));
    }

    @Test
    public void createExCodes_Returns_CorrectBlobBlock() {
        //Given
        final List<String> codes = Collections.singletonList("16485801412>-<5T7X9861");

        //When
        final List<ExtendedCode> extendedCodes = extendedCodeFactory.createExCodes(codes);

        //Then
        ExtendedCode.Block block3 = extendedCodes.get(0).getBlock(3);
        Assert.assertThat(block3.isBlob(), is(true));
        Assert.assertThat(block3.getRelativePosition(), is(RelativePosition.ABOVE));
    }


    @Test(expected = IllegalArgumentException.class)
    public void createExCodes_Throws_ExceptionForWrongCode() {
        //Given
        final List<String> codes = Arrays.asList("0996908468-093G001YG");

        //Then
        extendedCodeFactory.createExCodes(codes);
    }


    private TT080ExtendedCodeFactory createExtendedCodeFactory() {
        final TT080ExtendedCodeFactory extendedCodeFactory = new TT080ExtendedCodeFactory();
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