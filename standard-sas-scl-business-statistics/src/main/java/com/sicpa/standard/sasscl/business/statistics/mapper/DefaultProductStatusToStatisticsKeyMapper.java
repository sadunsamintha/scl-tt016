package com.sicpa.standard.sasscl.business.statistics.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;

/**
 * contains only the following <code>StatisticsKey</code>: <li>StatisticsKey.GOOD <li>StatisticsKey.BAD <br>
 * more to be added by customisation
 * 
 * @author DIelsch
 * 
 */
public class DefaultProductStatusToStatisticsKeyMapper implements IProductStatusToStatisticKeyMapper {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProductStatusToStatisticsKeyMapper.class);

	protected final Multimap<ProductStatus, Class<? extends StatisticsKey>> mapping = ArrayListMultimap.create();

	@SuppressWarnings("unchecked")
	public DefaultProductStatusToStatisticsKeyMapper() {
		add(ProductStatus.UNREAD, StatisticsKeyBad.class);
		add(ProductStatus.NOT_AUTHENTICATED, StatisticsKeyBad.class);
		add(ProductStatus.TYPE_MISMATCH, StatisticsKeyBad.class);
		add(ProductStatus.SENT_TO_PRINTER_UNREAD, StatisticsKeyBad.class);

		add(ProductStatus.AUTHENTICATED, StatisticsKeyGood.class);
		add(ProductStatus.EXPORT, StatisticsKeyGood.class);
		add(ProductStatus.REFEED, StatisticsKeyGood.class);
		add(ProductStatus.MAINTENANCE, StatisticsKeyGood.class);
	}

	@Override
	public Collection<StatisticsKey> getAllKeys() {
		Collection<StatisticsKey> res = new HashSet<StatisticsKey>();
		for (Class<? extends StatisticsKey> clazz : mapping.values()) {
			try {
				res.add(clazz.newInstance());
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return res;
	}

	@Override
	public Collection<StatisticsKey> getKey(final ProductStatus status) {
		Collection<Class<? extends StatisticsKey>> tmp = mapping.get(status);
		if (tmp == null) {
			tmp = new ArrayList<Class<? extends StatisticsKey>>();
			tmp.add(StatisticsKeyGood.class);
		}

		Collection<StatisticsKey> res = new ArrayList<StatisticsKey>();
		for (Class<? extends StatisticsKey> clazz : tmp) {
			try {
				StatisticsKey key = clazz.newInstance();
				key.setLine("");
				res.add(key);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return res;
	}

	@Override
	public void add(ProductStatus status, Class<? extends StatisticsKey>... keys) {
		if (keys == null) {
			return;
		}
		for (Class<? extends StatisticsKey> key : keys) {
			mapping.put(status, key);
		}
	}

}
