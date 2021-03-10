package com.sicpa.standard.sasscl.controller.productionconfig.factory.mapping;

import com.sicpa.standard.sasscl.controller.productionconfig.ID900CameraFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.config.D900CameraType;

public class D900CameraFactoryMapping implements ID900CameraFactoryMapping {

	@Override
	public String getD900CameraBeanName(D900CameraType cameraType) {

		if (cameraType.equals(D900CameraType.D900)) {
			return "d900Camera";
		}
		throw new IllegalArgumentException(cameraType + " not handled");

	}

}
