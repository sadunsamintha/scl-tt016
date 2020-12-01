package com.sicpa.standard.sasscl.business.statistics.mapper;

import static com.sicpa.standard.sasscl.model.ProductStatus.AUTHENTICATED;
import static com.sicpa.standard.sasscl.model.ProductStatus.COUNTING;
import static com.sicpa.standard.sasscl.model.ProductStatus.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductStatus.MAINTENANCE;
import static com.sicpa.standard.sasscl.model.ProductStatus.NOT_AUTHENTICATED;
import static com.sicpa.standard.sasscl.model.ProductStatus.REFEED;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_UNREAD;
import static com.sicpa.standard.sasscl.model.ProductStatus.TYPE_MISMATCH;
import static com.sicpa.standard.sasscl.model.ProductStatus.UNREAD;
import static com.sicpa.standard.sasscl.model.ProductStatus.INK_DETECTED;

import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.BAD;
import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.GOOD;
import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.BLOB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;

/**
 * contains only the following <code>StatisticsKey</code>: <li>StatisticsKey.GOOD <li>StatisticsKey.BAD <br>
 * more to be added by customisation
 * 
 * @author DIelsch
 * 
 */
public class DefaultProductStatusToStatisticsKeyMapper implements IProductStatusToStatisticKeyMapper {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProductStatusToStatisticsKeyMapper.class);

	protected final Multimap<ProductStatus, StatisticsKey> mapping = ArrayListMultimap.create();

	public DefaultProductStatusToStatisticsKeyMapper() {
		add(UNREAD, BAD);
		add(NOT_AUTHENTICATED, BAD);
		add(TYPE_MISMATCH, BAD);
		add(SENT_TO_PRINTER_UNREAD, BAD);

		add(AUTHENTICATED, GOOD);
		add(EXPORT, GOOD);
		add(REFEED, GOOD);
		add(COUNTING, GOOD);
		add(MAINTENANCE, GOOD);
		add(INK_DETECTED, GOOD);
	    add(INK_DETECTED, BLOB);
	}

	@Override
	public Collection<StatisticsKey> getAllKeys() {
		Collection<StatisticsKey> res = new HashSet<>();
		for (StatisticsKey key : mapping.values()) {
			try {
				res.add(cloneStatsKey(key));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return res;
	}

	@Override
	public Collection<StatisticsKey> getKey(ProductStatus status) {
		Collection<StatisticsKey> keys = mapping.get(status);
		if (keys == null) {
			throw new IllegalArgumentException("no statistics key for product status:" + status);
		}

		Collection<StatisticsKey> res = new ArrayList<>();
		keys.forEach((statKeyClazz) -> res.add(cloneStatsKey(statKeyClazz)));

		return res;
	}

	private StatisticsKey cloneStatsKey(StatisticsKey key) {
		return new StatisticsKey(key.getDescription());
	}

	@Override
	public void add(ProductStatus status, StatisticsKey key) {
		mapping.put(status, key);
	}

}
