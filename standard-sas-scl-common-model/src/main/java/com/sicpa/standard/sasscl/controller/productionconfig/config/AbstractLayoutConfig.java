package com.sicpa.standard.sasscl.controller.productionconfig.config;

import com.sicpa.standard.client.common.utils.StringMap;

public abstract class AbstractLayoutConfig {

	protected final StringMap properties = new StringMap();

	protected String id;

	public AbstractLayoutConfig(String id) {
		this.id = id;
	}

	public AbstractLayoutConfig() {
	}

	public StringMap getProperties() {
		return properties;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
