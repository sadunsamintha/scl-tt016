package com.sicpa.standard.sasscl.controller.view.event;

import com.sicpa.standard.sasscl.model.ProductionSendingProgress;

public class ProductionSendingProgressEvent {

	protected ProductionSendingProgress progress;

	public ProductionSendingProgressEvent() {
	}

	public ProductionSendingProgressEvent(ProductionSendingProgress progress) {
		this.progress = progress;
	}

	public ProductionSendingProgress getProgress() {
		return progress;
	}

	public void setProgress(ProductionSendingProgress progress) {
		this.progress = progress;
	}

}
