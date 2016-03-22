package com.sicpa.tt018.scl.utils;

@SuppressWarnings("serial")
public class ValidatorException extends Exception {

	private Object[] args;

	public ValidatorException() {
		super();
	}

	public ValidatorException(String message, Object... args) {
		super(message);
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

}
