package com.sicpa.standard.sasscl.business.statistics;

import java.util.Collection;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public interface IStatisticsKeyToViewDescriptorMapping {

	Collection<ViewStatisticsDescriptor> getDescriptor(StatisticsKey key);

}
