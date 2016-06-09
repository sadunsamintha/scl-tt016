package com.sicpa.standard.sasscl.business.sku.alert;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledOverTimeAlertTask;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;

public class UnkownSkuIdentifiedScheduledAlert extends AbstractScheduledOverTimeAlertTask {

	private boolean enabled;
	private int delaySec;
	private int maxUnreadCount;
	private int sampleSize;

	@Subscribe
	public void handleSkuNotdentifiedEvent(SkuNotRecognizedEvent evt) {
		addCounterToGrid();
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
