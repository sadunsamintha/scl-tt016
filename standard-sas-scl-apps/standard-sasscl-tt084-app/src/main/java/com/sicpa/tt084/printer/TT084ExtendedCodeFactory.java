package com.sicpa.tt084.printer;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.*;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
public class TT084ExtendedCodeFactory implements IExCodeBehavior {

    public final static String BLOCK_SEPARATOR = ">-<";
    private static final int NUMBER_OF_BLOCKS = 2;

    private ModelDataMatrixFormat dmFormat;
    private ModelDataMatrixEncoding dmEncoding;
    private RelativePosition blobPosition;
    private Option blobType;

    private int text1Length;
    private int text2Length;
    private RelativePosition text1Position;
    private RelativePosition text2Position;
    private boolean hrdEnable;

    private static final Logger logger = LoggerFactory.getLogger(TT084ExtendedCodeFactory.class);

    private BlobDetectionUtils blobUtils;
    private static final int BITMAP_CODE_HRC_HEIGHT = 9;

    @Override
    public List<ExtendedCode> createExCodes(List<String> codes) {
        Validate.notNull(codes);

        boolean isBlobPatternPrinted = blobUtils.isBlobPatternPrinted();
        logger.debug("Creating extended codes with isProductionBlobDetectionEnable : {}", isBlobPatternPrinted);
        List<ExtendedCode> res = new ArrayList<>();
        ExtendedCodeFactory ecf = new ExtendedCodeFactory();
        ecf.setBlockFactories(createBlockFactories(isBlobPatternPrinted));

        for (String c : codes) {
            res.add(ecf.create(createCompositeCode(c, isBlobPatternPrinted)));
        }

        return res;
    }

    @Override
    public List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes) {
        Validate.notNull(codes);

        boolean isBlobPatternPrinted = blobUtils.isBlobPatternPrinted();
        logger.debug("Creating extended codes with isProductionBlobDetectionEnable : {}", isBlobPatternPrinted);
        List<ExtendedCode> res = new ArrayList<>();
        ExtendedCodeFactory ecf = new ExtendedCodeFactory();
        ecf.setBlockFactories(createBlockFactories(isBlobPatternPrinted));

        for (Pair<String, String> c : codes) {
            res.add(ecf.create(createCompositeCodePair(c, isBlobPatternPrinted)));
        }

        return res;
    }

    private List<Object> createCompositeCode(String code, boolean isBlobEnabled) {

        List<Object> compositeCode = new ArrayList<>();

        if (hrdEnable) {

            compositeCode.add(TT084SicpaDataExCodeUtil.getBitmapLogo());
            compositeCode.add(code);

        } else {
            compositeCode.add(code);
        }

        if (isBlobEnabled) {
            /*
             * The blobFactory give us a default implementation of the blob pattern. Nevertheless we need to provide a
             * composite code due to xcode api design.
             */
            compositeCode.add(getDummyBlobData());
        }

        return compositeCode;
    }

    private List<Object> createCompositeCodePair(Pair<String, String> code, boolean isBlobEnabled) {
        List<Object> compositeCode = new ArrayList<>();

        if (hrdEnable) {
            if (code.getValue1() == null || code.getValue1().equals("")) {
                throw new IllegalArgumentException("code does not have 1st block.");
            }
            if (code.getValue2() == null || code.getValue2().equals("")) {
                throw new IllegalArgumentException("code does not have 2nd block.");
            }

            compositeCode.add(getTextLine1(code.getValue2()));
            compositeCode.add(getTextLine2(code.getValue2()));
            compositeCode.add(code.getValue1());
        } else {
            if (code.getValue1() == null || code.getValue1().equals("")) {
                throw new IllegalArgumentException("code does not have 1st block.");
            }
            compositeCode.add(code.getValue1());
        }

        if (isBlobEnabled) {
            /*
             * The blobFactory give us a default implementation of the blob pattern. Nevertheless we need to provide a
             * composite code due to xcode api design.
             */
            compositeCode.add(getDummyBlobData());
        }

        return compositeCode;
    }

    private String getDummyBlobData() {
        return "dummy";
    }

    private String getTextLine2(String[] parsedCode) {
        return parsedCode[1].substring(text1Length, parsedCode[1].length());
    }

    private String getTextLine1(String[] parsedCode) {
        return parsedCode[1].substring(0, text1Length);
    }

    private String getTextLine2(String textCode) {
        return textCode.substring(text1Length, textCode.length());
    }

    private String getTextLine1(String textCode) {
        return textCode.substring(0, text1Length);
    }

    private String getSicpadata(String[] parsedCode) {
        return parsedCode[0];
    }

    private List<BlockFactory> createBlockFactories(boolean isBlobEnable) {
        List<BlockFactory> blockFactories = new ArrayList<>();

        if (hrdEnable) {
            blockFactories.add(createLogoBitmapBlockFactory());
        }

        blockFactories.add(createDatamatrixBlockFactory());
        if (isBlobEnable) {
            blockFactories.add(createBlobBlockFactory());
        }

        return blockFactories;
    }

    private TextBlockFactory createTextLine2BlockFactory() {
        TextBlockFactory text2Factory = new TextBlockFactory();
        text2Factory.setRelativePosition(text2Position);
        text2Factory.setLength(text2Length);
        return text2Factory;
    }

    private TextBlockFactory createTextLine1BlockFactory() {
        TextBlockFactory text1Factory = new TextBlockFactory();
        text1Factory.setRelativePosition(text1Position);
        text1Factory.setLength(text1Length);
        return text1Factory;

    }

    private BlobBlockFactory createBlobBlockFactory() {
        BlobBlockFactory blobFactory = new BlobBlockFactory();
        blobFactory.setRelativePosition(blobPosition);
        blobFactory.addOption(blobType);

        return blobFactory;
    }

    private DatamatrixBlockFactory createDatamatrixBlockFactory() {
        DatamatrixBlockFactory dmFactory = new DatamatrixBlockFactory();
        dmFactory.setModelDatamatrixEncoding(dmEncoding);
        dmFactory.setModelDatamatrixFormat(dmFormat);
        return dmFactory;
    }

    private BitmapBlockFactory createLogoBitmapBlockFactory() {
        BitmapBlockFactory drcLogoBf = new BitmapBlockFactory();
        drcLogoBf.setHeight(BITMAP_CODE_HRC_HEIGHT);
        return drcLogoBf;
    }
}

