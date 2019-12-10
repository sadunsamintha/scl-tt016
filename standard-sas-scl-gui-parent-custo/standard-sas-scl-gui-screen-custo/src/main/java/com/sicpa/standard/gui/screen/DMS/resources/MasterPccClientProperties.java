package com.sicpa.standard.gui.screen.DMS.resources;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public abstract class MasterPccClientProperties extends Properties {

	private static final long serialVersionUID = -1L;

	protected String fileName;

	public MasterPccClientProperties() {
	}

	public void loadProperties() throws Exception {
		try {
			InputStream inputFile = MasterPccClientResourcesUtils.getConfigResource(this.fileName).openStream();
			this.load(inputFile);
			inputFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void saveConfiguration() throws Exception {
		OutputStream outputFile = null;
		// save properties
		try {
			outputFile = new FileOutputStream(this.fileName);
			store(outputFile, null);
			outputFile.flush();
		} catch (Exception e) {
			throw new Exception("Error during save file:" + this.fileName);
		} finally {
			outputFile.close();
		}
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}
}
