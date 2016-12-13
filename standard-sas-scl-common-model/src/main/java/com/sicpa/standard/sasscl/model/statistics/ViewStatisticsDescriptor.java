package com.sicpa.standard.sasscl.model.statistics;

import java.awt.*;

public class ViewStatisticsDescriptor {

	private Color color;
	private String key;
	private String line;
	private int index;
	private boolean countTowardsTotal = true;

	public ViewStatisticsDescriptor() {
	}

	public ViewStatisticsDescriptor(Color color, String key, int index, boolean countTowardsTotal) {
		this.color = color;
		this.key = key;
		this.index = index;
		this.countTowardsTotal = countTowardsTotal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ViewStatisticsDescriptor that = (ViewStatisticsDescriptor) o;

		if (index != that.index) return false;
		if (countTowardsTotal != that.countTowardsTotal) return false;
		if (color != null ? !color.equals(that.color) : that.color != null) return false;
		if (key != null ? !key.equals(that.key) : that.key != null) return false;
		return line != null ? line.equals(that.line) : that.line == null;
	}

	@Override
	public int hashCode() {
		int result = color != null ? color.hashCode() : 0;
		result = 31 * result + (key != null ? key.hashCode() : 0);
		result = 31 * result + (line != null ? line.hashCode() : 0);
		result = 31 * result + index;
		result = 31 * result + (countTowardsTotal ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ViewStatisticsDescriptor [color=" + color + ", key=" + key + ", line=" + line + ", index=" + index
				+ ", countTowardsTotal=" + countTowardsTotal + "]";
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isCountTowardsTotal() {
		return countTowardsTotal;
	}

	public void setCountTowardsTotal(boolean countTowardsTotal) {
		this.countTowardsTotal = countTowardsTotal;
	}
}
