package com.sicpa.standard.sasscl.devices.printer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.printer.controller.model.ModelDataMatrixEncoding;
import com.sicpa.standard.printer.controller.model.ModelDataMatrixFormat;
import com.sicpa.standard.printer.xcode.BitmapBlockFactory;
import com.sicpa.standard.printer.xcode.BlobBlockFactory;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.DatamatrixBlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.printer.xcode.Option;
import com.sicpa.standard.printer.xcode.RelativePosition;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class TT053SicpaDataAndBlobExCodeBehavior implements IExCodeBehavior {

	private ModelDataMatrixFormat dmFormat;
	private ModelDataMatrixEncoding dmEncoding;
	private Option dmOrientation;
	private RelativePosition blobPosition;
	private Option blobType;
	private static final Logger logger = LoggerFactory.getLogger(TT053SicpaDataAndBlobExCodeBehavior.class);
	private static final int BITMAP_CODE_HRC_HEIGHT = 7;

	private BlobDetectionUtils blobUtils;
	private ProductionParameters productionParameters;

	@Override
	public List<ExtendedCode> createExCodes(List<String> codes) {
		Validate.notNull(codes);

		boolean isBlobPatternPrinted = blobUtils.isBlobPatternPrinted();
		logger.debug("Creating extended codes with isProductionBlobDetectionEnable : {}", isBlobPatternPrinted);
		List<ExtendedCode> res = new ArrayList<>();
		ExtendedCodeFactory ecf = new ExtendedCodeFactory();
		if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
			ecf.setBlockFactories(createExportBlockFactories());
		} else {
			ecf.setBlockFactories(createBlockFactories(isBlobPatternPrinted));
		}

		for (String c : codes) {
			if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
				long[] bmp = TT053SicpaDataExCodeUtil.getExportBitmapLogo();
				res.add(ecf.create(createExpCompositeCode(bmp)));

			} else {
				res.add(ecf.create(createCompositeCode(c, isBlobPatternPrinted)));
			}
		}

		return res;
	}

	private List<Object> createCompositeCode(String code, boolean isBlobEnabled) {

		List<Object> compositeCode = new ArrayList<>();

		compositeCode.add(code);
		if (isBlobEnabled) {
			/*
			 * The blobFactory give us a default implementation of the blob
			 * pattern. Nevertheless we need to provide a composite code due to
			 * xcode api design.
			 */
			compositeCode.add(getDummyBlobData());
		}

		return compositeCode;
	}

	private List<Object> createExpCompositeCode(long[] bitMap) {
		List<Object> compositeCode = new ArrayList<>();
		compositeCode.add(bitMap);
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

	private List<BlockFactory> createExportBlockFactories() {
		List<BlockFactory> blockFactories = new ArrayList<>();
		blockFactories.add(createBitmapBlockFactory());
		return blockFactories;
	}

	private BlobBlockFactory createBlobBlockFactory() {
		BlobBlockFactory blobFactory = new BlobBlockFactory();
		blobFactory.setRelativePosition(blobPosition);
		blobFactory.addOption(blobType);

		return blobFactory;
	}

	private BitmapBlockFactory createBitmapBlockFactory() {
		BitmapBlockFactory textBf = new BitmapBlockFactory();
		textBf.addOption(Option.REVERSE);
		textBf.setHeight(BITMAP_CODE_HRC_HEIGHT);
		return textBf;
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

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
