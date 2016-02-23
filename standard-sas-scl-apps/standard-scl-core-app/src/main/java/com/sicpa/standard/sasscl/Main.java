package com.sicpa.standard.sasscl;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;

public class Main {

	private static Logger logger;

	static {
		File log = new File("./log");

		if (!log.exists()) {
			log.mkdir();
		}

		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(final String[] args) {
		LoadApplicationScreen.DOUBLE_BUFFERING_OFF = false;

		SicpaLookAndFeel.install();

		MainAppWithProfile app = new MainAppWithProfile() {

			@Override
			public String getApplicationVersion() {
				InputStream versionFile = null;
				try {
					versionFile = ClassLoader.getSystemResourceAsStream("version");
					return IOUtils.toString(versionFile);
				} catch (Exception e) {
					logger.error("", e);
					return "N/A";
				} finally {
					if (versionFile != null) {
						try {
							versionFile.close();
						} catch (Exception e2) {
						}
					}
				}
			}
		};
		app.selectProfile();
	}
}
