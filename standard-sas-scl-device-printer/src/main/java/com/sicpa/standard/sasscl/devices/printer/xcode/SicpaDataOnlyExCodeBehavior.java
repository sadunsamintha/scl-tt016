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
import com.sicpa.standard.printer.xcode.Option;

public class SicpaDataOnlyExCodeBehavior implements IExCodeBehavior {

	protected ModelDataMatrixEncoding dmEncoding;
	protected ModelDataMatrixFormat dmFormat;
	protected Option dmOrientation;

	public SicpaDataOnlyExCodeBehavior() {
		dmEncoding = ModelDataMatrixEncoding.ASCII;
		dmFormat = ModelDataMatrixFormat.DM_8x18;
	}

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

	protected List<Object> createCompositeCode(String dm,
			List<BlockFactory> factories) {
		return Arrays.asList(dm);
	}

	protected List<BlockFactory> createBlockFactories() {
		DatamatrixBlockFactory dm = new DatamatrixBlockFactory();
		dm.setModelDatamatrixEncoding(dmEncoding);
		dm.setModelDatamatrixFormat(dmFormat);
		if (dmOrientation != null) {
			dm.addOption(dmOrientation);
		}
		return Arrays.asList(dm);
	}

	public void setDmEncoding(ModelDataMatrixEncoding dmEncoding) {
		this.dmEncoding = dmEncoding;
	}

	public void setDmFormat(ModelDataMatrixFormat dmFormat) {
		this.dmFormat = dmFormat;
	}

	public Option getDmOrientation() {
		return dmOrientation;
	}

	public void setDmOrientation(Option dmOrientation) {
		this.dmOrientation = dmOrientation;
	}

}
