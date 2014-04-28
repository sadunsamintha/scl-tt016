package com.sicpa.standard.sasscl.view.productionSending;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class ProductionSendingViewModel extends AbstractObservableModel {

	protected int batchCount;
	protected int currentBatch;

	public ProductionSendingViewModel(int batchCount, int currentBatch) {
		this.batchCount = batchCount;
		this.currentBatch = currentBatch;
	}

	public ProductionSendingViewModel() {
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public int getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(int currentBatch) {
		this.currentBatch = currentBatch;
	}

}
