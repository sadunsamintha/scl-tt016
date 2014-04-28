package com.sicpa.standard.sasscl.controller.process;

public class ProductionStartValidatorResult {

	public static ProductionStartValidatorResult createValidResult() {
		return new ProductionStartValidatorResult(true, null);
	}

	public static ProductionStartValidatorResult createInvalidResult(String message) {
		return new ProductionStartValidatorResult(false, message);
	}

	protected boolean valid;
	protected String message;

	protected ProductionStartValidatorResult(boolean valid, String message) {
		this.valid = valid;
		this.message = message;
	}

	public boolean isValid() {
		return valid;
	}

	public String getMessage() {
		return message;
	}
}
