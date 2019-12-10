package com.sicpa.standard.gui.screen.DMS.resources;

import java.net.URL;

public class MasterPccClientResourcesUtils {
	public static URL getConfigResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("config/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}

	public static URL getReportResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("report/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}

	public static URL getResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("resource/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}

	public static URL getLanguageResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("lang/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}
}
