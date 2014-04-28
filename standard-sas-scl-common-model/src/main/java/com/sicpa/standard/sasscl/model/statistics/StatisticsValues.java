package com.sicpa.standard.sasscl.model.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

public class StatisticsValues implements Serializable {

	private static final long serialVersionUID = 1L;

	protected final Map<StatisticsKey, Integer> mapValues = new HashMap<StatisticsKey, Integer>();

	public StatisticsValues() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapValues == null) ? 0 : mapValues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticsValues other = (StatisticsValues) obj;
		if (mapValues == null) {
			if (other.mapValues != null)
				return false;
		} else if (!mapValues.equals(other.mapValues))
			return false;
		return true;
	}

	public void increase(final StatisticsKey key, final int i) {
		synchronized (mapValues) {
			Integer current = mapValues.get(key);
			if (current == null) {
				current = 0;
			}
			current += i;
			mapValues.put(key, current);
		}
	}

	public void set(final StatisticsKey key, final int i) {
		synchronized (mapValues) {
			mapValues.put(key, i);
		}
	}

	public void increase(final StatisticsKey key) {
		increase(key, 1);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int get(final StatisticsKey key) {
		synchronized (mapValues) {
			Integer value = mapValues.get(key);
			if (value == null) {
				value = 0;
			}
			return value;
		}
	}

	public Map<StatisticsKey, Integer> getMapValues() {
		return mapValues;
	}

	public void reset() {
		synchronized (mapValues) {
			mapValues.clear();
		}
	}
}
