package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import com.sicpa.standard.sasscl.model.Code;

public class BeforeActivationResult {

	private Code code;
	private boolean valid;
	private boolean filtered;

	public BeforeActivationResult(Code code, boolean valid, boolean filtered) {
		this.code = code;
		this.valid = valid;
		this.filtered = filtered;
	}

	public static BeforeActivationResult createBeforeActivationResultFiltered(Code code, boolean valid) {
		final boolean isFiltered = true;
		return new BeforeActivationResult(code, valid, isFiltered);
	}

	public static BeforeActivationResult createBeforeActivationResultNotFiltered(Code code, boolean valid) {
		final boolean isFiltered = false;
		return new BeforeActivationResult(code, valid, isFiltered);
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isFiltered() {
		return filtered;
	}
}
