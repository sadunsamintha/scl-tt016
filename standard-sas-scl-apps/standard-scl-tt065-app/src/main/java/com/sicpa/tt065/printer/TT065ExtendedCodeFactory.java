package com.sicpa.tt065.printer;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.BlobBlockFactory;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.DatamatrixBlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.printer.xcode.TextBlockFactory;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;

import static com.sicpa.tt065.remote.impl.sicpadata.TT065SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;


public class TT065ExtendedCodeFactory implements IExCodeBehavior {

    private ModelDataMatrixFormat dmFormat;
    private ModelDataMatrixEncoding dmEncoding;
    private RelativePosition blobPosition;
    private int text1Length;
    private RelativePosition text1Position;
    private int text2Length;
    private RelativePosition text2Position;

    private boolean hrdEnable = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(TT065ExtendedCodeFactory.class);

    private BlobDetectionUtils blobUtils;

    private static final int NUMBER_OF_BLOCKS =  2;

    @Override
    public List<ExtendedCode> createExCodes(List<String> codes) {
        Validate.notNull(codes);

        final boolean isBlobEnable = blobUtils.isBlobPatternPrinted();
        LOGGER.debug("Creating extended codes with isBlobEnable : {}", isBlobEnable);
        List<ExtendedCode> res = new ArrayList<>();
        ExtendedCodeFactory ecf = new ExtendedCodeFactory();
        ecf.setBlockFactories(createBlockFactories(isBlobEnable));

        for (String c : codes) {
            res.add(ecf.create(createCompositeCode(c, isBlobEnable)));
        }

        return res;
    }

    @Override
    public List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes) {
        LOGGER.warn("This action is not yet supported for this class");
        throw new UnsupportedOperationException("This action is not yet supported for this class");
    }

    private List<Object> createCompositeCode(final String code, final boolean isBlobEnable) {
        String[] parsedCode = code.split(BLOCK_SEPARATOR);

        /* If the string code is not composed of 2 blocks with a block separator then
           we throw an exception  */

        if(parsedCode.length != NUMBER_OF_BLOCKS) {
            throw new IllegalArgumentException("code with value " + parsedCode + " is not composed of 2 blocks." );
        }

        List<Object> compositeCode = new ArrayList<>();

        if(hrdEnable) {
            compositeCode.add(getTextLine1(parsedCode));
            compositeCode.add(getTextLine2(parsedCode));
        }
        compositeCode.add(getSicpadata(parsedCode));
        if(isBlobEnable) {
            /*
               The blobFactory give us a default implementation of the blob pattern.
               Nevertheless we need to provide a composite code due to xcode api design.
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

    private String getSicpadata(String[] parsedCode) {
        return parsedCode[0];
    }

    private List<BlockFactory> createBlockFactories(boolean isBlobEnable) {
        List<BlockFactory> blockFactories = new ArrayList<>();

        if(hrdEnable) {
            blockFactories.add(createTextLine1BlockFactory());
            blockFactories.add(createTextLine2BlockFactory());
        }
        blockFactories.add(createDatamatrixBlockFactory());
        if(isBlobEnable) {
            blockFactories.add(createBlobBlockFactory());
        }

        return blockFactories;
    }

    private BlobBlockFactory createBlobBlockFactory() {
        BlobBlockFactory blobFactory = new BlobBlockFactory();
        blobFactory.setRelativePosition(blobPosition);
        return blobFactory;
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

    private DatamatrixBlockFactory createDatamatrixBlockFactory() {
        DatamatrixBlockFactory dmFactory = new DatamatrixBlockFactory();
        dmFactory.setModelDatamatrixEncoding(dmEncoding);
        dmFactory.setModelDatamatrixFormat(dmFormat);
        return dmFactory;
    }

    public void setDmFormat(ModelDataMatrixFormat dmFormat) {
        this.dmFormat = dmFormat;
    }

    public void setDmEncoding(ModelDataMatrixEncoding dmEncoding) {
        this.dmEncoding = dmEncoding;
    }

    public void setText1Length(int text1Length) {
        this.text1Length = text1Length;
    }

    public void setText1Position(RelativePosition text1Position) {
        this.text1Position = text1Position;
    }

    public void setText2Length(int text2Length) {
        this.text2Length = text2Length;
    }

    public void setText2Position(RelativePosition text2Position) {
        this.text2Position = text2Position;
    }

    public void setBlobPosition(RelativePosition blobPosition) {
        this.blobPosition = blobPosition;
    }


    public void setBlobUtils(BlobDetectionUtils blobUtils) {
        this.blobUtils = blobUtils;
    }

    public void setHrdEnable(boolean hrdEnable) {
        this.hrdEnable = hrdEnable;
    }
}
