package com.sicpa.standard.sasscl.business.statistics;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

import java.awt.*;
import java.util.Collection;

public interface IStatisticsKeyToViewDescriptorMapping {

	Collection<ViewStatisticsDescriptor> getDescriptor(StatisticsKey key);

	void add(StatisticsKey key, Color color, int index, String langKey, boolean countTowardsTotal);

}
