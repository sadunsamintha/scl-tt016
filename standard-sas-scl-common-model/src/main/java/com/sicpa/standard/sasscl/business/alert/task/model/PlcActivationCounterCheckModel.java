package com.sicpa.standard.sasscl.business.alert.task.model;

public class PlcActivationCounterCheckModel extends AbstractAlertTaskModel {
	private int maxDelta;

	public int getMaxDelta() {
		return maxDelta;
	}

	public void setMaxDelta(int maxDelta) {
		this.maxDelta = maxDelta;
	}

}