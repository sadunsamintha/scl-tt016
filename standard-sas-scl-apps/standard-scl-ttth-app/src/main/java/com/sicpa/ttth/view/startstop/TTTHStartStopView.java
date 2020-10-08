package com.sicpa.ttth.view.startstop;

import com.sicpa.standard.sasscl.view.startstop.IStartStopViewListener;
import com.sicpa.standard.sasscl.view.startstop.StartStopView;

@SuppressWarnings("serial")
public class TTTHStartStopView extends StartStopView {

	private IStartStopViewListener productionListener;

	@Override
	public void initGUI() {
		super.initGUI();
		addListener(productionListener);
	}

	public void setProductionListener(IStartStopViewListener productionListener) {
		this.productionListener = productionListener;
	}
}
