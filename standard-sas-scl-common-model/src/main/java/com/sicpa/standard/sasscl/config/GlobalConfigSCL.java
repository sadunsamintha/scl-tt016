package com.sicpa.standard.sasscl.config;

public class GlobalConfigSCL extends GlobalConfig {

	private static final long serialVersionUID = 1L;

	/**
	 * date when the application will start to download encoder for next year
	 */
	protected String downloadNextYearEncodersDate;

	/**
	 * Minimum number of encoders in storage.
	 */
	protected int minEncodersThreshold;

	protected int requestNumberEncoders;

	public GlobalConfigSCL() {
		super();
	}

	public GlobalConfigSCL(String file) {
		super(file);
	}

	public int getMinEncodersThreshold() {
		return this.minEncodersThreshold;
	}

	public void setMinEncodersThreshold(final int minEncodersThreshold) {
		this.minEncodersThreshold = minEncodersThreshold;
	}

	public int getRequestNumberEncoders() {
		return this.requestNumberEncoders;
	}

	public void setRequestNumberEncoders(final int requestNumberEncoders) {
		this.requestNumberEncoders = requestNumberEncoders;
	}

	public String getDownloadNextYearEncodersDate() {
		return this.downloadNextYearEncodersDate;
	}

	public void setDownloadNextYearEncodersDate(final String downloadNextYearEncodersDate) {
		this.downloadNextYearEncodersDate = downloadNextYearEncodersDate;
	}

	@Override
	public void setDefaultValues() {
		super.setDefaultValues();
		setRequestNumberEncoders(10);
		setMinEncodersThreshold(5);
	}

}
