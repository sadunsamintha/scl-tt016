package com.sicpa.standard.sasscl.view.monitoring.advanced;

import java.util.Map;
import java.util.TreeMap;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class PlcMonitoringModel extends AbstractObservableModel {

	private final Map<String, String> valueByVar = new TreeMap<>();

	private boolean monitoringActive;

	public void put(String key, String value) {
		synchronized (valueByVar) {
			valueByVar.put(key, value);
		}
	}

	public Map<String, String> getValueByVar() {
		return valueByVar;
	}

	public boolean isMonitoringActive() {
		return monitoringActive;
	}

	public void setMonitoringActive(boolean monitoringActive) {
		this.monitoringActive = monitoringActive;
	}
}
