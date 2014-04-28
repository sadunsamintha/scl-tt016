package com.sicpa.standard.sasscl.controller.productionconfig.config;

/**
 * config of printer related to the current selected production parameter, part of the production config
 * 
 * @author DIelsch
 * 
 */
public class PrinterConfig extends AbstractLayoutConfig {

	protected PrinterType printerType;
	protected String validatedBy;

	public PrinterConfig(String id, PrinterType type) {
		super(id);
		this.printerType = type;
	}

	public PrinterConfig() {
	}

	public void setPrinterType(PrinterType printerType) {
		this.printerType = printerType;
	}

	public PrinterType getPrinterType() {
		return printerType;
	}

	public void setValidatedBy(String validatedBy) {
		this.validatedBy = validatedBy;
	}

	public String getValidatedBy() {
		return validatedBy;
	}
}
