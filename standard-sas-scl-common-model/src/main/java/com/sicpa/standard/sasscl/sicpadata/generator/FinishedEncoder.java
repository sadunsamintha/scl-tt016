package com.sicpa.standard.sasscl.sicpadata.generator;


public class FinishedEncoder implements IFinishedEncoder {

	private static final long serialVersionUID = 1L;

	protected IEncoder encoder;

	public FinishedEncoder(IEncoder encoder) {
		this.encoder = encoder;
	}

	public FinishedEncoder(IEncoder encoder, boolean sentToRemoveServer) {
		this.encoder = encoder;
	}

	@Override
	public IEncoder getEncoder() {
		return encoder;
	}
}
