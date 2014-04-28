package com.sicpa.standard.sasscl.business.coding;

public class CodeReceivedFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CodeReceivedFailedException() {
	}

	public CodeReceivedFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeReceivedFailedException(String message) {
		super(message);
	}

	public CodeReceivedFailedException(Throwable cause) {
		super(cause);
	}

}
