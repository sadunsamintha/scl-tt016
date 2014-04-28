package com.sicpa.standard.sasscl.business.statistics;

import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;

public interface IStatistics {

	/**
	 * @return the number of product for each statisticsKey
	 */
	StatisticsValues getValues();

	/**
	 * reset the statistics values to 0
	 */
	void reset();

	/**
	 * return the number of second the production has been running
	 */
	int getUptime();

}
