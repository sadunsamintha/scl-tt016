package com.sicpa.tt016.printer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.leibinger.utils.BitmapBuilder;
import com.sicpa.standard.printer.xcode.DotBitmap;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.printer.xcode.TextFontType;
import com.sicpa.standard.sasscl.blobDetection.BlobDetectionState;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionProvider;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;

public class TT016ExtendedCodeBitmapTest {

    @Test
    public void extended_code_create_bitmap() throws Exception {
        List<ExtendedCode> extendedCodes = createExtendedCode();
        sendExtendedCodes(extendedCodes);
    }

    private void sendExtendedCodes(List<ExtendedCode> extendedCodeList) throws PrinterException {
        List<String> dataBlockList = new ArrayList<>();
        BitmapBuilder bitmapBuilder = new BitmapBuilder(TextFontType.PIXEL_7x5);

        int counter = 0;
        for (ExtendedCode xCode : extendedCodeList) {
            DotBitmap db = bitmapBuilder.buildBitmap(xCode, counter);
            String bmAsString = db.asHexString().toUpperCase();
            String blockList = db.getHeight() + "." + db.getLength() + "." + bmAsString;
            dataBlockList.add(blockList);
            counter++;
        }

    }

    private List<ExtendedCode> createExtendedCode() {
        TT016ExtendedCodeFactory extendedCodeFactory = createExtendedCodeFactory();
        List<Pair<String, String>> codesPair = new ArrayList<>();
        codesPair.add(new Pair<String, String>("099>-<8468", "093G001YG"));

        return extendedCodeFactory.createExCodesPair(codesPair);
    }

    private TT016ExtendedCodeFactory createExtendedCodeFactory() {
        TT016ExtendedCodeFactory extendedCodeFactory = new TT016ExtendedCodeFactory();
        extendedCodeFactory.setDmFormat(ModelDataMatrixFormat.DM_8x18);
        extendedCodeFactory.setDmEncoding(ModelDataMatrixEncoding.ASCII);
        extendedCodeFactory.setBlobPosition(RelativePosition.ABOVE);
        extendedCodeFactory.setText1Length(4);
        extendedCodeFactory.setText1Position(RelativePosition.RIGHT);
        extendedCodeFactory.setText2Length(4);
        extendedCodeFactory.setText2Position(RelativePosition.BELOW);

        BlobDetectionProvider provider = new BlobDetectionProvider() {
            @Override
            public BlobDetectionState getProductionBlobDetectionState() {
                return new BlobDetectionState() {
                    @Override
                    public boolean isBlobDetectionEnable() {
                        return false;
                    }
                };
            }

            @Override
            public boolean isBlobDetectionEnable() {
                return false;
            }

            @Override
            public boolean isBlobPatternPrinted() {
                return false;
            }
        };

        BlobDetectionUtils blobUtils = new BlobDetectionUtils(provider);
        extendedCodeFactory.setBlobUtils(blobUtils);

        return extendedCodeFactory;
    }




}
