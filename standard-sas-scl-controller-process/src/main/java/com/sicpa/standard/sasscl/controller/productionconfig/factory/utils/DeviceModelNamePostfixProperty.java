package com.sicpa.standard.sasscl.controller.productionconfig.factory.utils;

import com.sicpa.standard.sasscl.controller.productionconfig.IDeviceModelNamePostfixProperty;

public class DeviceModelNamePostfixProperty implements IDeviceModelNamePostfixProperty {

	protected String postfix;

	@Override
	public void set(String postfix) {
		this.postfix = postfix;
	}

	@Override
	public String get() {
		return postfix;
	}

}
