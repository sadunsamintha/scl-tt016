package com.sicpa.standard.sasscl.business.statistics.mapper;

import java.util.Collection;

import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;

/**
 * do the mapping between product status and statistics key
 * 
 * @author DIelsch
 * 
 */
public interface IProductStatusToStatisticKeyMapper {

	/**
	 * return a list of <code>StatisticsKey</code> corresponding to the give <code>ProductStatus</code><br>
	 * it can return a List because for example a NO_INK status could have to increase the BAD and NO_INK counter
	 */
	Collection<StatisticsKey> getKey(ProductStatus status);

	void add(ProductStatus status, StatisticsKey key);

	Collection<StatisticsKey> getAllKeys();
}
