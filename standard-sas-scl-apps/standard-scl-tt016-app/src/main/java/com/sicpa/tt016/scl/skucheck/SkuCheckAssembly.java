package com.sicpa.tt016.scl.skucheck;

public class SkuCheckAssembly {

	private long transactionId;
	private long start, end;

	public SkuCheckAssembly() {

	}

	public void setTransactionId(long id) {
		this.transactionId = id;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
}
