package com.sicpa.standard.sasscl.view.monitoring.advanced;

import java.util.Map;
import java.util.TreeMap;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class PlcMonitoringModel extends AbstractObservableModel {

	private final Map<String, String> variableValueMap = new TreeMap<>();

	private boolean monitoringActive;

	public String get(String key) {
		return variableValueMap.get(key);
	}

	public void put(String key, String value) {
		variableValueMap.put(key, value);
	}

	public Map<String, String> getVariableValueMap() {
		return variableValueMap;
	}

	public boolean isMonitoringActive() {
		return monitoringActive;
	}

	public void setMonitoringActive(boolean monitoringActive) {
		this.monitoringActive = monitoringActive;
	}
}
