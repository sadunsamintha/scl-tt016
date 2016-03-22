package com.sicpa.standard.sasscl.view.main.statistics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public class StatisticsKeyToViewDescriptorMapping implements IStatisticsKeyToViewDescriptorMapping {

	private final Multimap<String, ViewStatisticsDescriptor> mapping = ArrayListMultimap.create();

	public StatisticsKeyToViewDescriptorMapping() {
		add(StatisticsKey.GOOD, SicpaColor.GREEN_DARK, 0, "stats.display.good");
		add(StatisticsKey.BAD, SicpaColor.RED, 1, "stats.display.bad");
	}

	@Override
	public void add(StatisticsKey key, Color color, int index, String langKey) {
		mapping.put(key.getDescription(), new ViewStatisticsDescriptor(color, langKey, index));
	}

	@Override
	public Collection<ViewStatisticsDescriptor> getDescriptor(StatisticsKey key) {
		Collection<ViewStatisticsDescriptor> tmp = mapping.get(key.getDescription());
		if (tmp == null) {
			tmp = Collections.emptyList();
		}

		Collection<ViewStatisticsDescriptor> res = new ArrayList<>();
		for (ViewStatisticsDescriptor desc : tmp) {
			res.add(clone(desc));
		}
		return res;
	}

	private ViewStatisticsDescriptor clone(ViewStatisticsDescriptor desc) {
		return new ViewStatisticsDescriptor(desc.getColor(), desc.getKey(), desc.getIndex());
	}
}
