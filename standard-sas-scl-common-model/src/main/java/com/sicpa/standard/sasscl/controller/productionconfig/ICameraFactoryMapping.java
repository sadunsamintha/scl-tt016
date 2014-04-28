package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraType;


public interface ICameraFactoryMapping {

	String getCameraBeanName(CameraType cameraType);
}
