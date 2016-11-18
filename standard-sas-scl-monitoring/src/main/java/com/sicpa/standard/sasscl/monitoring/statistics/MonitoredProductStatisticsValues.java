package com.sicpa.standard.sasscl.monitoring.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;

public class MonitoredProductStatisticsValues implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Map<StatisticsKey, Integer> values;
	protected Map<StatisticsKey, Integer> mapOffset;

	public MonitoredProductStatisticsValues() {
		this.values = new HashMap<>();
		this.mapOffset = new HashMap<>();
	}

	public Map<StatisticsKey, Integer> getValues() {
		return this.values;
	}

	public void setValues(final StatisticsValues values) {
		this.values = values.getMapValues();
	}

	public void setMapOffset(final Map<StatisticsKey, Integer> mapOffset) {
		this.mapOffset = new HashMap<>(mapOffset);
	}

	@Override
	public String toString() {
		// (total:12)(bad:1)(good:11)
		String s = "";
		if (this.values != null) {
			for (Entry<StatisticsKey, Integer> entry : this.values.entrySet()) {

				Integer offset = this.mapOffset.get(entry.getKey());
				if (offset == null) {
					offset = 0;
				}
				int delta = entry.getValue() - offset;
				s += "(" + entry.getKey() + ":" + delta + ")";
			}
		}
		return s;
	}

	public Map<StatisticsKey, Integer> getMapOffset() {
		return this.mapOffset;
	}
}
