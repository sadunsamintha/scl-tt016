package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.camera.controller.ICameraController;
import com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs;
import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.business.activation.impl.ActivationWithPostPackage;
import com.sicpa.standard.sasscl.business.postPackage.PostPackage;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class ActivationWithPostPackageTestSCL extends ActivationTestSCL {

	public ActivationWithPostPackageTestSCL() {
		PropertyPlaceholderResources.addProperties("postPackage", PostPackage.class.getName());
		PropertyPlaceholderResources.addProperties("activation", ActivationWithPostPackage.class.getName());
	}

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfigSCL config = new SpringConfigSCL();
		return config;
	}

	@Override
	public void onErrorCameraCodeReceived(ICameraController<?> arg0, ErrorCodeEventArgs arg1) {
		dataGenerated.add("SENT_TO_PRINTER_UNREAD" + codes.get(counter) + "SKU#1");
		counter++;
	}
}
