package com.sicpa.tt065.view.flow;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;

import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.*;
import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.BATCH_ID_REGISTERED;
import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.PRODUCTION_PARAMETER_TO_BATCH;

public class TT065DefaultScreensFlow extends DefaultScreensFlow {

	protected IScreenGetter batchIdScreen;

	public TT065DefaultScreensFlow() {

	}

	/**
	 * build the flow, called by spring
	 */
	@Override
	public void buildScreensFlow() {

		addTransitions(mainScreen,
				new ScreenTransition(REQUEST_SELECTION, selectionScreen),
				new ScreenTransition(EXIT, exitScreen));

		addTransitions(selectionScreen,
				new ScreenTransition(PRODUCTION_PARAMETER_TO_BATCH, batchIdScreen),
				new ScreenTransition(BATCH_ID_REGISTERED, mainScreen),
				new ScreenTransition(EXIT, exitScreen));
	
		addTransitions(exitScreen);
	}

	public void setBatchIdScreen(IScreenGetter batchIdScreen) {
		this.batchIdScreen = batchIdScreen;
	}
}