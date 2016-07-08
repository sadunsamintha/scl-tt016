package com.sicpa.tt016.business.statistics;

import com.sicpa.standard.sasscl.business.statistics.mapper.DefaultProductStatusToStatisticsKeyMapper;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.statistics.TT016StatisticsKey;

public class TT016ProductStatusToStatisticsKeyMapper extends DefaultProductStatusToStatisticsKeyMapper {

	public TT016ProductStatusToStatisticsKeyMapper() {
		add(TT016ProductStatus.EJECTED_PRODUCER, TT016StatisticsKey.EJECTED_PRODUCER);
	}
}
