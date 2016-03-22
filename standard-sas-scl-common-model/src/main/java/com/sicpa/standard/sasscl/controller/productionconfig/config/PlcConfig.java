package com.sicpa.standard.sasscl.controller.productionconfig.config;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.client.common.utils.StringMap;

/**
 * plc config related to the production
 * 
 * @author DIelsch
 * 
 */
public class PlcConfig extends AbstractLayoutConfig {

	private final StringMap cabinetProperties = new StringMap();
	private final Map<Integer, StringMap> linesProperties = new HashMap<>();

	public StringMap getCabinetProperties() {
		return cabinetProperties;
	}

	public Map<Integer, StringMap> getLinesProperties() {
		return linesProperties;
	}

}
