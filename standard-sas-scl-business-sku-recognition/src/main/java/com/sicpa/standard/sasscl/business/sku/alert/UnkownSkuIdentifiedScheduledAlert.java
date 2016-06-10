package com.sicpa.standard.sasscl.business.sku.alert;

import java.time.Instant;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledOverTimeAlertTask;
import com.sicpa.standard.sasscl.business.sku.IProductionChangeDetector;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;

public class UnkownSkuIdentifiedScheduledAlert extends AbstractScheduledOverTimeAlertTask {

	private boolean enabled;
	private int delaySec;
	private int maxUnreadCount;
	private int sampleSize;
	private IProductionChangeDetector productionChangeDetector;
	private Instant previousSkuEventTime;

	@Subscribe
	public void handleSkuNotdentifiedEvent(SkuNotRecognizedEvent evt) {
		handleMaxDurationBetweenEvent();
		previousSkuEventTime = Instant.now();
		addCounterToGrid();
	}

	@Subscribe
	public void handleSkuRecognizedEvent(SkuRecognizedEvent evt) {
		handleMaxDurationBetweenEvent();
		previousSkuEventTime = Instant.now();
	}

	private void handleMaxDurationBetweenEvent() {
		if (isTimeExceededSinceLastRecognizedSku()) {
			reset();
		}
	}

	private boolean isTimeExceededSinceLastRecognizedSku() {
		return productionChangeDetector.isPossibleProductionChange(previousSkuEventTime, Instant.now());
	}

	@Override
	public long getDelay() {
		return delaySec * 1000;
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.SKU_IDENTIFICATION_TOO_MANY_UNKNOWN);
	}

	@Override
	protected boolean isEnabledDefaultImpl() {
		return enabled;
	}

	@Override
	public String getAlertName() {
		return "TooManyUnkownSkuIdentified";
	}

	@Override
	protected int getThreshold() {
		return maxUnreadCount;
	}

	@Override
	protected int getSampleSize() {
		return sampleSize;
	}

	public void setMaxUnreadCount(int maxUnreadCount) {
		this.maxUnreadCount = maxUnreadCount;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDelaySec(int delaySec) {
		this.delaySec = delaySec;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
}
