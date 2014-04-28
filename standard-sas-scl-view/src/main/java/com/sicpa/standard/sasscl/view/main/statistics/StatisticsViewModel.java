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

	protected final Map<ViewStatisticsDescriptor, Integer> mapStats = new HashMap<ViewStatisticsDescriptor, Integer>();
	protected final Map<String, String> mapSpeed = new TreeMap<String, String>();

	protected int total = 0;

	// in second
	protected int uptime;

	public void setSpeed(String line, String speed) {
		mapSpeed.put(line, speed);
	}

	public Map<String, String> getLineSpeed() {
		return mapSpeed;
	}

	public Map<ViewStatisticsDescriptor, Integer> getStatistics(String line) {
		synchronized (mapStats) {
			Map<ViewStatisticsDescriptor, Integer> res = new HashMap<ViewStatisticsDescriptor, Integer>();
			for (Entry<ViewStatisticsDescriptor, Integer> entry : mapStats.entrySet()) {
				if (entry.getKey().getLine().equals(line)) {
					res.put(entry.getKey(), entry.getValue());
				}
			}
			return res;
		}
	}

	public Collection<String> getLineIndexes() {
		Collection<String> res = new HashSet<String>();
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
		this.uptime = uptime;
	}

	public int getUptime() {
		return uptime;
	}

	public int getStatisticsDescriptorCount() {
		return mapStats.size();
	}

	public void reset() {
		mapSpeed.clear();
		mapStats.clear();
		total = 0;
		uptime = 0;
	}
}
