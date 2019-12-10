package com.sicpa.standard.gui.screen.DMS.resources;

public class MasterPccClientConfig extends MasterPccClientProperties {

	private static final long serialVersionUID = -1L;

	private static final String CONFIGURATION_FILE_NAME = "config.properties";
	private static final String UPDATE_URL = "updateUrl";

	public MasterPccClientConfig() throws Exception {
		this.fileName = MasterPccClientConfig.CONFIGURATION_FILE_NAME;
		try {
			loadProperties();
		} catch (Exception e) {
			throw new Exception("Error when loading Properties file");
		}
	}

	public String getValue(final String key) {
		return getProperty(key);
	}

	public long getValueAsLong(final String key) {
		return Long.parseLong(getProperty(key));
	}

	public int getValueAsInt(final String key) {
		return Integer.parseInt(getProperty(key));
	}

	public float getValueAsFloat(final String key) {
		return Float.parseFloat(getProperty(key));
	}

	public boolean getValueAsBoolean(final String key) {
		return Boolean.parseBoolean(getProperty(key));
	}

	public String getUpdateURL() {
		String updateURL = getValue(UPDATE_URL);
		if (updateURL == null) {
			return null;
		}
		if (!updateURL.endsWith("/")) {
			updateURL += "/";
		}
		return updateURL;
	}
}
