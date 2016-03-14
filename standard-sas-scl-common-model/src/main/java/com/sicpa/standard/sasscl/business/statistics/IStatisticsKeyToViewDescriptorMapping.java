package com.sicpa.standard.sasscl.business.statistics;

import java.awt.Color;
import java.util.Collection;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public interface IStatisticsKeyToViewDescriptorMapping {

	Collection<ViewStatisticsDescriptor> getDescriptor(StatisticsKey key);

	void add(StatisticsKey key, Color color, int index,String langKey);

}
