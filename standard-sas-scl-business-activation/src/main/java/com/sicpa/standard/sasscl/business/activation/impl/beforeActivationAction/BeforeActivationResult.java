package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import com.sicpa.standard.sasscl.model.Code;

public class BeforeActivationResult {

	protected Code code;
	protected boolean valid;
	protected boolean filtered;

	public BeforeActivationResult() {
	}

	public BeforeActivationResult(Code code, boolean valid) {
		this(code, valid, false);
	}

	public BeforeActivationResult(Code code, boolean valid, boolean filtered) {
		this.code = code;
		this.valid = valid;
		this.filtered = filtered;
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
