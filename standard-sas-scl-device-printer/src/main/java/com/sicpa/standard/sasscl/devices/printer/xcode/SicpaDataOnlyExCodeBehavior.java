package com.sicpa.standard.sasscl.devices.printer.xcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.DatamatrixBlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;

public class SicpaDataOnlyExCodeBehavior implements IExCodeBehavior {

	@Override
	public List<ExtendedCode> createExCodes(List<String> codes) {

		List<ExtendedCode> res = new ArrayList<>();
		ExtendedCodeFactory ecf = new ExtendedCodeFactory();
		ecf.setBlockFactories(createBlockFactories());

		for (String c : codes) {
			res.add(ecf.create(createCompositeCode(c, ecf.getBlockFactories())));
		}

		return res;
	}

	protected List<Object> createCompositeCode(String dm, List<BlockFactory> factories) {
		return Arrays.asList(dm);
	}

	protected List<BlockFactory> createBlockFactories() {
		DatamatrixBlockFactory dm = new DatamatrixBlockFactory();
		dm.setModelDatamatrixEncoding(ModelDataMatrixEncoding.ASCII);
		dm.setModelDatamatrixFormat(ModelDataMatrixFormat.DM_8x18);
		return Arrays.asList(dm);
	}

}
