package com.sicpa.tt016.view.main;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.view.main.statistics.StatisticsKeyToViewDescriptorMapping;
import com.sicpa.tt016.model.statistics.TT016StatisticsKey;

public class TT016StatisticsKeyToViewDescriptorMapping extends StatisticsKeyToViewDescriptorMapping {

	public TT016StatisticsKeyToViewDescriptorMapping() {
		super();
		add(TT016StatisticsKey.EJECTED_PRODUCER, SicpaColor.RED, 3, "stats.display.ejectedProducer");
	}
}
