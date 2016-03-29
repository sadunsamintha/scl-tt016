package com.sicpa.tt018.scl.camera.simulator;

import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.tt018.scl.utils.AlbaniaCameraConstants;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;

public class AlbaniaCameraSimulatorController extends CameraSimulatorController {

	public static final String CAMERA_ERROR_BLOB_DETECTION_CODE_STRING = "100";
	public static final String CAMERA_ERROR_COMMON_CODE_STRING = "0";
	public static final Code CAMERA_ERROR_BLOB_DETECTION_CODE = new Code(CAMERA_ERROR_BLOB_DETECTION_CODE_STRING);

	private static final Random randomGenerator = new Random();

	public AlbaniaCameraSimulatorController(AlbaniaCameraSimulatorConfig config) {
		super(config);
		setCustoCodesGeneratedTransformer(t -> generateCode(t));
	}

	private Pair<Boolean, String> generateCode(String intputCode) {

		boolean valid;
		String code = intputCode;
		int randomVal = randomGenerator.nextInt(100);
		if (isInGoodPercentageRange(randomVal) && AlbaniaUtilities.isDomesticMode(productionParameters)) {
			valid = true;
			System.out.println("code good fired : " + code + " :");
		} else if (isInBlobPercentageRange(randomVal) && AlbaniaUtilities.isDomesticMode(productionParameters)) {
			code = AlbaniaCameraConstants.CAMERA_ERROR_BLOB_DETECTION_CODE_STRING;
			valid = false;
			System.out.println("code bad blob fired : " + code + " :");
		} else {
			// bad code percentage range
			code = AlbaniaCameraConstants.CAMERA_ERROR_COMMON_CODE_STRING;
			valid = false;
			System.out.println("code bad fired : " + code + " :");
		}
		return Pair.of(valid, code);
	}

	private boolean isInGoodPercentageRange(int randomVal) {
		return randomVal >= getCameraSimulatorConfig().getPercentageBadCode() && !isInBlobPercentageRange(randomVal);
	}

	private boolean isInBlobPercentageRange(int randomVal) {
		return randomVal >= (100 - getCameraSimulatorConfig().getPercentageBlobCode());
	}

	private AlbaniaCameraSimulatorConfig getCameraSimulatorConfig() {
		return (AlbaniaCameraSimulatorConfig) getCameraModel();
	}

}
