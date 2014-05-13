package com.sicpa.standard.sasscl.business.coding;

/**
 * Event that defines the number of codes requested by the printer.
 * 
 */
public class RequestExtendedCodesEvent {

	protected long numberCodes;

	protected ICodeReceiver target;

	public RequestExtendedCodesEvent(final long numberCodes, ICodeReceiver target) {
		this.numberCodes = numberCodes;
		this.target = target;
	}

	public RequestExtendedCodesEvent(final long numberCodes) {
		this(numberCodes, null);
	}

	public long getNumberCodes() {
		return numberCodes;
	}

	public ICodeReceiver getTarget() {
		return target;
	}
}
