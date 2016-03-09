package com.sicpa.standard.sasscl.view.main.statistics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public class StatisticsViewModel extends AbstractObservableModel {

	private final Map<ViewStatisticsDescriptor, Integer> mapStats = new HashMap<>();
	private final Map<Integer, String> mapSpeed = new TreeMap<>();

	private int total = 0;
	private int uptimeInSec;

	public void setSpeed(int line, String speed) {
		mapSpeed.put(line, speed);
	}

	public Map<Integer, String> getLineSpeed() {
		return mapSpeed;
	}

	public Map<ViewStatisticsDescriptor, Integer> getStatistics(String line) {
		synchronized (mapStats) {
			Map<ViewStatisticsDescriptor, Integer> res = new HashMap<>();
			for (Entry<ViewStatisticsDescriptor, Integer> entry : mapStats.entrySet()) {
				if (entry.getKey().getLine().equals(line)) {
					res.put(entry.getKey(), entry.getValue());
				}
			}
			return res;
		}
	}

	public Collection<String> getLineIndexes() {
		Collection<String> res = new HashSet<>();
		for (Entry<ViewStatisticsDescriptor, Integer> entry : mapStats.entrySet()) {
			res.add(entry.getKey().getLine());
		}
		return res;
	}

	public void setStatistics(ViewStatisticsDescriptor statsKey, int qty) {
		synchronized (mapStats) {
			mapStats.put(statsKey, qty);
		}
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setUptime(int uptime) {
		this.uptimeInSec = uptime;
	}

	public int getUptime() {
		return uptimeInSec;
	}

	public int getStatisticsDescriptorCount() {
		return mapStats.size();
	}

	public void reset() {
		mapSpeed.clear();
		mapStats.clear();
		total = 0;
		uptimeInSec = 0;
	}
}
