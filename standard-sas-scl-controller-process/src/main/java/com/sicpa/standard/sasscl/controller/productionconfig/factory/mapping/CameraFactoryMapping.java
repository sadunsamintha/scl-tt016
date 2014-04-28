package com.sicpa.standard.sasscl.controller.productionconfig.factory.mapping;

import com.sicpa.standard.sasscl.controller.productionconfig.ICameraFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraType;

public class CameraFactoryMapping implements ICameraFactoryMapping {

	@Override
	public String getCameraBeanName(CameraType cameraType) {

		if (cameraType.equals(CameraType.COGNEX)) {
			return "camera";
		}
		if (cameraType.equals(CameraType.DRS)) {
			return "cameraDrs";
		}
		throw new IllegalArgumentException(cameraType + " not handled");

	}

}
