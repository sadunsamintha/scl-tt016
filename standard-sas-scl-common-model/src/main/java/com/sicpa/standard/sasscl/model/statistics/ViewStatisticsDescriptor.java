package com.sicpa.standard.sasscl.model.statistics;

import java.awt.Color;

public class ViewStatisticsDescriptor {

	@Override
	public String toString() {
		return "VStatsDesc[key=" + key + ", line=" + line + ", index=" + index + "]";
	}

	protected Color color;
	protected String key;
	protected int line;
	protected int index;

	public ViewStatisticsDescriptor() {
	}

	public ViewStatisticsDescriptor(Color color, String key, int index) {
		this.color = color;
		this.key = key;
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + index;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + line;
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
		ViewStatisticsDescriptor other = (ViewStatisticsDescriptor) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (index != other.index)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (line != other.line)
			return false;
		return true;
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

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
