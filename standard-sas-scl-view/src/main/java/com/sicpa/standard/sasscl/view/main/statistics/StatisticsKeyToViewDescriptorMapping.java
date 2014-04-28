package com.sicpa.standard.sasscl.view.main.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public class StatisticsKeyToViewDescriptorMapping implements IStatisticsKeyToViewDescriptorMapping {

	protected final Multimap<Class<? extends StatisticsKey>, ViewStatisticsDescriptor> mapping = ArrayListMultimap
			.create();

	public StatisticsKeyToViewDescriptorMapping() {
		mapping.put(StatisticsKeyGood.class, new ViewStatisticsDescriptor(SicpaColor.GREEN_DARK, "stats.display.good", 0));
		mapping.put(StatisticsKeyBad.class, new ViewStatisticsDescriptor(SicpaColor.RED, "stats.display.bad", 1));
	}

	@Override
	public Collection<ViewStatisticsDescriptor> getDescriptor(StatisticsKey key) {
		Collection<ViewStatisticsDescriptor> tmp = mapping.get(key.getClass());
		if (tmp == null) {
			tmp = Collections.emptyList();
		}

		Collection<ViewStatisticsDescriptor> res = new ArrayList<ViewStatisticsDescriptor>();
		for (ViewStatisticsDescriptor desc : tmp) {
			res.add(clone(desc));
		}
		return res;
	}

	protected ViewStatisticsDescriptor clone(ViewStatisticsDescriptor desc) {
		return new ViewStatisticsDescriptor(desc.getColor(), desc.getKey(), desc.getIndex());
	}
}
