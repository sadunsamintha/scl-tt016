package com.sicpa.standard.sasscl.common.utils;

import java.util.Date;

public class ComparatorUtils {

	public static class DateComparator implements java.util.Comparator<Date> {

		@Override
		public int compare(Date o1, Date o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return o1.compareTo(o2);
		}
	}
}
