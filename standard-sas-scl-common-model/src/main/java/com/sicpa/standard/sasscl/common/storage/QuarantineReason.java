package com.sicpa.standard.sasscl.common.storage;

public class QuarantineReason {

	public static final QuarantineReason LOAD_ERROR = new QuarantineReason("error-other", "LOAD_ERROR");
	public static final QuarantineReason REMOTE_SERVER_BUSINESS_ERROR = new QuarantineReason("error-business",
			"REMOTE_SERVER_BUSINESS_ERROR");
	public static final QuarantineReason INVALID_ENCODER = new QuarantineReason("error-business", "INVALID_ENCODER");

	protected String filePrefix;
	protected String subFolder;

	public QuarantineReason(String subFolder, String filePrefix) {
		this.filePrefix = filePrefix;
		this.subFolder = subFolder;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public String getSubFolder() {
		return subFolder;
	}
}
