package com.sicpa.standard.gui.screen.DMS.resources;

public abstract class MasterPccClientLangage extends MasterPccClientProperties {

	private static final long serialVersionUID = 1L;

	public MasterPccClientLangage() throws Exception {
		loadLanguage();
		if (this.fileName == null || this.fileName.isEmpty()) {
			throw new IllegalStateException("fileName can't be null or empty, call setFileName in loadLanguage");
		}
		loadProperties();
	}

	/**
	 * example Default language is English<br>
	 * if (language.equals(DSLanguage.LANGUAGE_SPANISH)){<br>
	 * fileName = DSLanguage.SPANISH_LANGUAGE_FILE_NAME;<br>
	 * Locale.setDefault(new Locale("es", "VE"));<br>
	 * }<br>
	 * else{<br>
	 * fileName = DSLanguage.ENGLISH_LANGUAGE_FILE_NAME;<br>
	 * Locale.setDefault(Locale.US);<br>
	 * }
	 */
	protected abstract void loadLanguage();

	public String getValue(final String key) {
		try {
			String value = getProperty(key);
			if (value != null) {
				return value;
			} else {
				System.err.println("No translation for: " + key);
				return key;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
}
