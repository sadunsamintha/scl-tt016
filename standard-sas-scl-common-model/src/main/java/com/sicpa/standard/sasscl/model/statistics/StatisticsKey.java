package com.sicpa.standard.sasscl.model.statistics;

import java.io.Serializable;

public class StatisticsKey implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static StatisticsKey TOTAL = new ReadOnlyStatisticsKey("total");
	public final static StatisticsKey GOOD = new ReadOnlyStatisticsKey("good");
	public final static StatisticsKey BAD = new ReadOnlyStatisticsKey("bad");
	public final static StatisticsKey BLOB = new ReadOnlyStatisticsKey("blobActivated"); 

	private String description;
	private String line;

	public StatisticsKey(String description) {
		this.description = description;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return line + " " + description;
	}

	public String getLine() {
		return line;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
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
		StatisticsKey other = (StatisticsKey) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}

	public static class ReadOnlyStatisticsKey extends StatisticsKey {
		private static final long serialVersionUID = 1L;

		public ReadOnlyStatisticsKey(String description) {
			super(description);
		}

		@Override
		public void setLine(String line) {
			throw new IllegalAccessError(
					StatisticsKey.class.getName()
							+ ".GOOD/BAD are template and should not be used directly, create a new key using their description");
		}
	}
}
