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

	private final Map<ViewStatisticsDescriptor, Integer> qtyByStatsKey = new HashMap<>();
	private final Map<Integer, String> speedByLineIndex = new TreeMap<>();
	private int total = 0;
	private int uptimeInSec;

	private boolean totalVisible;
	private boolean lineSpeedVisible;
	private final Map<String, Boolean> visibilityByStatisticsKey = new HashMap<>();

	public void setSpeed(int line, String speed) {
		speedByLineIndex.put(line, speed);
	}

	public Map<Integer, String> getLineSpeed() {
		return speedByLineIndex;
	}

	public Map<ViewStatisticsDescriptor, Integer> getStatistics(String line) {
		synchronized (qtyByStatsKey) {
			Map<ViewStatisticsDescriptor, Integer> res = new HashMap<>();
			for (Entry<ViewStatisticsDescriptor, Integer> entry : qtyByStatsKey.entrySet()) {
				if (entry.getKey().getLine().equals(line)) {
					res.put(entry.getKey(), entry.getValue());
				}
			}
			return res;
		}
	}

	public Collection<String> getLineIndexes() {
		Collection<String> res = new HashSet<>();
		for (Entry<ViewStatisticsDescriptor, Integer> entry : qtyByStatsKey.entrySet()) {
			res.add(entry.getKey().getLine());
		}
		return res;
	}

	public void setStatistics(ViewStatisticsDescriptor statsKey, int qty) {
		synchronized (qtyByStatsKey) {
			qtyByStatsKey.put(statsKey, qty);
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
		return qtyByStatsKey.size();
	}

	public void reset() {
		speedByLineIndex.clear();
		qtyByStatsKey.clear();
		total = 0;
		uptimeInSec = 0;
	}

	public void setTotalVisible(boolean totalVisible) {
		this.totalVisible = totalVisible;
	}

	public boolean isTotalVisible() {
		return totalVisible;
	}

	public void setLineSpeedVisible(boolean lineSpeedVisible) {
		this.lineSpeedVisible = lineSpeedVisible;
	}

	public boolean isLineSpeedVisible() {
		return lineSpeedVisible;
	}

	public void setStatisticsVisible(String statsKey, boolean visible) {
		visibilityByStatisticsKey.put(statsKey, visible);
	}

	public boolean isStatisticVisible(String statsKey) {
		return visibilityByStatisticsKey.getOrDefault(statsKey, true);
	}
}
