package com.sicpa.standard.sasscl.utils;

import java.io.IOException;
import java.net.URISyntaxException;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.controller.productionconfig.IDeviceModelNamePostfixProperty;

public class ConfigUtilEx {

	public static final String GLOBAL_PROPERTIES_PATH = "config/global.properties";

	public static Object load(String pattern, IDeviceModelNamePostfixProperty postfix) throws IOException,
			URISyntaxException {
		return ConfigUtils.load(pattern.replace("####", postfix.get()));
	}
}
