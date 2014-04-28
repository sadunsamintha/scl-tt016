package com.sicpa.standard.sasscl.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Properties;

public class BenchmarkConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String LAST_PARSED_FILE = "LAST_PARSED_FILE";

	private static Properties properties = new Properties();

	public static Properties getProperties() {
		return properties;
	}

	public static void putProperties(final String key, final String value) {
		properties.put(key, value);
	}

	public static String getProperty(final String key) {
		return properties.getProperty(key);
	}

	private static final String CONFIG_FILE = "config.properties";

	public static void save() {
		try {
			properties.store(new FileOutputStream(new File(CONFIG_FILE)), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			properties.load(new FileInputStream(new File(CONFIG_FILE)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
