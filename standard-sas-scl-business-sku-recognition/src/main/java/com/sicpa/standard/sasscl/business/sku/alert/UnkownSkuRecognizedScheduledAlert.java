package com.sicpa.standard.sasscl.business.sku.alert;

import java.time.Instant;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.AbstractBadCountAlert;
import com.sicpa.standard.sasscl.business.sku.IProductionChangeDetector;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;

public class UnkownSkuRecognizedScheduledAlert extends AbstractBadCountAlert {

	private boolean enabled;
	private IProductionChangeDetector productionChangeDetector;
	private Instant previousSkuEventTime;

	@Subscribe
	public void handleSkuNotRecognizedEvent(SkuNotRecognizedEvent evt) {
		handleMaxDurationBetweenEvent();
		previousSkuEventTime = Instant.now();
		increaseBad();
	}

	@Subscribe
	public void handleSkuRecognizedEvent(SkuRecognizedEvent evt) {
		handleMaxDurationBetweenEvent();
		previousSkuEventTime = Instant.now();
		increaseGood();
	}

	private void handleMaxDurationBetweenEvent() {
		if (isTimeExceededSinceLastRecognizedSku()) {
			reset();
		}
	}

	private boolean isTimeExceededSinceLastRecognizedSku() {
		if (previousSkuEventTime == null) {
			return false;
		}
		return productionChangeDetector.isPossibleProductionChange(previousSkuEventTime, Instant.now());
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.SKU_RECOGNITION_TOO_MANY_UNKNOWN);
	}

	@Override
	protected boolean isEnabledDefaultImpl() {
		return enabled;
	}

	@Override
	public String getAlertName() {
		return "TooManyUnkownRecognizedSku";
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setProductionChangeDetector(IProductionChangeDetector productionChangeDetector) {
		this.productionChangeDetector = productionChangeDetector;
	}
}
