package com.sicpa.standard.sasscl.business.coding.validator;

public class EncoderValidatorResult {

	protected boolean valid;
	protected String quarantineFilePrefix;

	public static EncoderValidatorResult createValidResult() {
		return new EncoderValidatorResult(true, null);
	}

	public static EncoderValidatorResult createInvalidResult(String quarantinePrefix) {
		return new EncoderValidatorResult(false, quarantinePrefix);
	}

	private EncoderValidatorResult(boolean valid, String quarantineFilePrefix) {
		this.valid = valid;
		this.quarantineFilePrefix = quarantineFilePrefix;
	}

	public EncoderValidatorResult(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public String getQuarantineFilePrefix() {
		return quarantineFilePrefix;
	}

}
