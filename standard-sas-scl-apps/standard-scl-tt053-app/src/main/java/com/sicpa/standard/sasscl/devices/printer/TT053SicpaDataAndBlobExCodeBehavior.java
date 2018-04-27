package com.sicpa.standard.sasscl.devices.printer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.sicpa.standard.printer.xcode.BitmapBlockFactory;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.printer.xcode.Option;
import com.sicpa.standard.sasscl.devices.printer.xcode.SicpaDataAndBlobExCodeBehavior;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class TT053SicpaDataAndBlobExCodeBehavior extends SicpaDataAndBlobExCodeBehavior {

	private static final int BITMAP_CODE_HRC_HEIGHT = 9;
	private ProductionParameters productionParameters;

	@Override
	public List<ExtendedCode> createExCodes(List<String> codes) {
		
		if(productionParameters.getProductionMode().equals(ProductionMode.EXPORT)){
			Validate.notNull(codes);
			List<ExtendedCode> res = new ArrayList<>();
			ExtendedCodeFactory ecf = new ExtendedCodeFactory();
			ecf.setBlockFactories(createExportBlockFactories());
			for (String c : codes) {
					long[] bmp = TT053SicpaDataExCodeUtil.getExportBitmapLogo();
					res.add(ecf.create(createExpCompositeCode(bmp)));
			}
			
			return res;
		}else{
			return super.createExCodes(codes);
		}
	}

	private List<Object> createExpCompositeCode(long[] bitMap) {
		List<Object> compositeCode = new ArrayList<>();
		compositeCode.add(bitMap);
		return compositeCode;
	}

	private List<BlockFactory> createExportBlockFactories() {
		List<BlockFactory> blockFactories = new ArrayList<>();
		blockFactories.add(createBitmapBlockFactory());
		return blockFactories;
	}

	private BitmapBlockFactory createBitmapBlockFactory() {
		BitmapBlockFactory textBf = new BitmapBlockFactory();
		textBf.addOption(Option.REVERSE);
		textBf.setHeight(BITMAP_CODE_HRC_HEIGHT);
		return textBf;
	}

}
