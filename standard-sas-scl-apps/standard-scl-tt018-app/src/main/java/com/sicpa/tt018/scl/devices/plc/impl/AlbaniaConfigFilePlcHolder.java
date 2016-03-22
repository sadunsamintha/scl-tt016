package com.sicpa.tt018.scl.devices.plc.impl;

import java.util.Map;

public class AlbaniaConfigFilePlcHolder {

	private Map<Integer, String> configFileByProductTypeMapping;

	public String getConfigFileByProduct(int productPackage) {
		return configFileByProductTypeMapping.get(productPackage);
	}

	public Map<Integer, String> getConfigFileByProductTypeMapping() {
		return configFileByProductTypeMapping;
	}

	public void setConfigFileByProductTypeMapping(Map<Integer, String> configFileByProductTypeMapping) {
		this.configFileByProductTypeMapping = configFileByProductTypeMapping;
	}

}
