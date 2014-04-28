package com.sicpa.standard.sasscl.model;

public class ProductionSendingProgress {

	protected int batchCount;
	protected int currentBatchIndex;
	
	public ProductionSendingProgress() {
	}

	public ProductionSendingProgress(int batchCount, int currentBatchIndex) {
		this.batchCount = batchCount;
		this.currentBatchIndex = currentBatchIndex;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public int getCurrentBatchIndex() {
		return currentBatchIndex;
	}

	public void setCurrentBatchIndex(int currentBatchIndex) {
		this.currentBatchIndex = currentBatchIndex;
	}

}
