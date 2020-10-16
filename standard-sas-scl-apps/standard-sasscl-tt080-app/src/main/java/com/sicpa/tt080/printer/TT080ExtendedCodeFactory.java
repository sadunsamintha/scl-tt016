package com.sicpa.tt080.printer;

import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.Validate;

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

import static com.sicpa.tt080.remote.impl.sicpadata.TT080SicpaDataGeneratorWrapper.NUMBER_OF_BLOCKS;
import static com.sicpa.tt080.remote.impl.sicpadata.TT080SicpaDataGeneratorWrapper.PRINTER_SPACE_REPRESENTATION;

@Slf4j
@Setter
public class TT080ExtendedCodeFactory implements IExCodeBehavior {

    private ModelDataMatrixFormat dmFormat;
    private ModelDataMatrixEncoding dmEncoding;
    private RelativePosition blobPosition;
    private int text1Length;
    private int text2Length;
    private RelativePosition text1Position;
    private RelativePosition text2Position;
    private boolean hrdEnable = true;
    private BlobDetectionUtils blobUtils;


    @Override
    public List<ExtendedCode> createExCodes(final List<String> codes) {
        Validate.notNull(codes);

        final boolean isBlobEnable = blobUtils.isBlobPatternPrinted();
        log.debug("Creating extended codes with isBlobEnable : {}", isBlobEnable);

        final List<ExtendedCode> res = new ArrayList<>();
        ExtendedCodeFactory ecf = new ExtendedCodeFactory();
        ecf.setBlockFactories(createBlockFactories(isBlobEnable));

        for (final String c : codes) {
            res.add(ecf.create(createCompositeCode(c, isBlobEnable)));
        }

        return res;
    }
    
    /**
   	 * This implementation is used in Morocco (TT016) and not in Dominican Republic (TT080)
   	 */
    @Override
	public List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes) {
		return null;
	}

    private List<Object> createCompositeCode(final String code, final boolean isBlobEnable) {
        final String[] parsedCode = code.split(PRINTER_SPACE_REPRESENTATION);

        if(hrdEnable && parsedCode.length != NUMBER_OF_BLOCKS) {
            throw new IllegalArgumentException("code with value " + parsedCode + " is not composed of "+NUMBER_OF_BLOCKS+" blocks.");
        }

        final List<Object> compositeCode = new ArrayList<>();

        if(hrdEnable) {
            compositeCode.add(getTextLine1(parsedCode));
            compositeCode.add(getTextLine2(parsedCode));
        }

        compositeCode.add(getSicpadata(parsedCode));

        if(isBlobEnable) {
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
}
