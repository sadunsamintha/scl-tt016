package com.sicpa.standard.sasscl.business.coding;

/**
 * Event that defines the number of codes requested by the printer.
 * 
 */
public class RequestCodesEvent {

	protected long numberCodes;

	protected ICodeReceiver target;

	public RequestCodesEvent(final long numberCodes, ICodeReceiver target) {
		this.numberCodes = numberCodes;
		this.target = target;
	}

	public RequestCodesEvent(final long numberCodes) {
		this(numberCodes, null);
	}

	public long getNumberCodes() {
		return numberCodes;
	}

	public ICodeReceiver getTarget() {
		return target;
	}
}
