package com.sicpa.tt018.scl.devices.printer;

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
import com.sicpa.standard.printer.xcode.Option;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;
import com.sicpa.tt018.scl.utils.AlbaniaBlobUtils;

public class TT018ExtendedCodeFactory implements IExCodeBehavior {
	private ModelDataMatrixFormat dmFormat;
	private ModelDataMatrixEncoding dmEncoding;
	private Option dmOrientation;
	private RelativePosition blobPosition;
	private Option blobType;
	private static final Logger logger = LoggerFactory.getLogger(TT018ExtendedCodeFactory.class);

	private AlbaniaBlobUtils blobUtils;

	@Override
	public List<ExtendedCode> createExCodes(List<String> codes) {
		Validate.notNull(codes);

		boolean isBlobEnable = blobUtils.isBlobEnable();
		logger.debug("Creating extended codes with isBlobEnable : {}", isBlobEnable);
		List<ExtendedCode> res = new ArrayList<>();
		ExtendedCodeFactory ecf = new ExtendedCodeFactory();
		ecf.setBlockFactories(createBlockFactories(isBlobEnable));

		for (String c : codes) {
			res.add(ecf.create(createCompositeCode(c, isBlobEnable)));
		}

		return res;
	}
	
	/**
	 * Interface IExCodeBehavior.java has a new method signature using codes in pairs
	 * as the parameter which is not called here in Albania (TT018)
	 * Its implementation is only used in Morocco (TT016)
	 */
	@Override
	public List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes) {
		return null;
	}

	private List<Object> createCompositeCode(String code, boolean isBlobEnable) {

		List<Object> compositeCode = new ArrayList<>();

		compositeCode.add(code);
		if (isBlobEnable) {
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

	private List<BlockFactory> createBlockFactories(boolean isBlobEnable) {
		List<BlockFactory> blockFactories = new ArrayList<>();

		blockFactories.add(createDatamatrixBlockFactory());
		if (isBlobEnable) {
			blockFactories.add(createBlobBlockFactory());
		}

		return blockFactories;
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
		if (dmOrientation != null) {
			dmFactory.addOption(dmOrientation);
		}
		return dmFactory;
	}

	public void setDmFormat(ModelDataMatrixFormat dmFormat) {
		this.dmFormat = dmFormat;
	}

	public void setDmEncoding(ModelDataMatrixEncoding dmEncoding) {
		this.dmEncoding = dmEncoding;
	}

	public void setBlobPosition(RelativePosition blobPosition) {
		this.blobPosition = blobPosition;
	}

	public void setBlobUtils(AlbaniaBlobUtils blobUtils) {
		this.blobUtils = blobUtils;
	}

	public Option getBlobType() {
		return blobType;
	}

	public void setBlobType(Option blobType) {
		this.blobType = blobType;
	}

	public Option getDmOrientation() {
		return dmOrientation;
	}

	public void setDmOrientation(Option dmOrientation) {
		this.dmOrientation = dmOrientation;
	}
}
