package com.sicpa.standard.gui.screen.machine.impl.SPL.CustomisationSample;

import com.sicpa.standard.gui.screen.machine.impl.SPL.SplViewController;

public class SampleCustomViewController extends SplViewController {

	public void addPrinterWarning(final int i) {
		((SampleCustomStatisticsModel) this.statsModel).addPrinterWarning(i);
	}

	public void resetPrinterWarning() {
		((SampleCustomStatisticsModel) this.statsModel).setPrinterWarning(0);
	}
}
