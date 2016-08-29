package com.sicpa.standard.sasscl.devices.printer.xcode;

import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.*;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SicpaDataAndBlobExCodeBehavior implements IExCodeBehavior {

	private ModelDataMatrixFormat dmFormat;
	private ModelDataMatrixEncoding dmEncoding;
	private Option dmOrientation;
	private RelativePosition blobPosition;
	private Option blobType;
	private static final Logger logger = LoggerFactory.getLogger(SicpaDataAndBlobExCodeBehavior.class);

	private BlobDetectionUtils blobUtils;

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

	public void setBlobType(Option blobType) {
		this.blobType = blobType;
	}

	public void setDmOrientation(Option dmOrientation) {
		this.dmOrientation = dmOrientation;
	}

	public void setBlobUtils(BlobDetectionUtils blobUtils) {
		this.blobUtils = blobUtils;
	}

}
