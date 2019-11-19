package com.sicpa.standard.gui.components.loggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogLevelUtils {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(LogLevelUtils.class);

	public static void loadConfig(final LogLevelPanelModel model, final File file) {
		if (file != null) {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file);

				Properties prop = new Properties();
				prop.load(fin);

				for (Logger logger : model.getLoggers()) {
					String sLevel = prop.getProperty(logger.getName());
					if (sLevel != null) {

						Level newLevel = null;
						if (sLevel.toUpperCase().equals("ERROR")) {
							newLevel = Level.ERROR;
						} else if (sLevel.toUpperCase().equals("WARN")) {
							newLevel = Level.WARN;
						} else if (sLevel.toUpperCase().equals("INFO")) {
							newLevel = Level.INFO;
						} else if (sLevel.toUpperCase().equals("DEBUG")) {
							newLevel = Level.DEBUG;
						}
						model.setLevel(logger, newLevel);
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public static void saveConfig(final File file) {
		if (file != null) {
			FileOutputStream fout = null;
			try {
				LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				Properties prop = new Properties();

				for (Logger logger : loggerContext.getLoggerList()) {
					if (logger.getLevel() != null) {
						prop.put(logger.getName(), logger.getLevel().toString());
					}
				}
				fout = new FileOutputStream(file);
				prop.store(fout, "");
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
}
