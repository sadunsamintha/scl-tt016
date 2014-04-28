package com.sicpa.standard.sasscl.controller.productionconfig.config;

/**
 * plc config related to the production
 * 
 * @author DIelsch
 * 
 */
public class PlcConfig extends AbstractLayoutConfig {

	protected String line1ConfigFile;
	protected String line2ConfigFile;
	protected String line3ConfigFile;

	protected String line1Index;
	protected String line2Index;
	protected String line3Index;

	public String getLine1ConfigFile() {
		return line1ConfigFile;
	}

	public String getLine2ConfigFile() {
		return line2ConfigFile;
	}

	public void setLine1ConfigFile(String line1ConfigFile) {
		this.line1ConfigFile = line1ConfigFile;
	}

	public void setLine2ConfigFile(String line2ConfigFile) {
		this.line2ConfigFile = line2ConfigFile;
	}

	public void setLine1Index(String line1Index) {
		this.line1Index = line1Index;
	}

	public void setLine2Index(String line2Index) {
		this.line2Index = line2Index;
	}

	public String getLine1Index() {
		return line1Index;
	}

	public String getLine2Index() {
		return line2Index;
	}

	public String getLine3ConfigFile() {
		return line3ConfigFile;
	}

	public String getLine3Index() {
		return line3Index;
	}

	public void setLine3ConfigFile(String line3ConfigFile) {
		this.line3ConfigFile = line3ConfigFile;
	}

	public void setLine3Index(String line3Index) {
		this.line3Index = line3Index;
	}
}
