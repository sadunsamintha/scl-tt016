package com.sicpa.tt079.view.flow;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.*;
import static com.sicpa.tt079.view.flow.TT079ScreenFlowTriggers.BATCH_ID_EXP_DT_REGISTERED;

/**
 * Overwritting class of DefaultScreensFlow to manage
 *
 */
public class TT079DefaultScreensFlow extends DefaultScreensFlow {

	protected IScreenGetter batchIdScreen;

	public TT079DefaultScreensFlow() {

	}

	/**
	 * build the flow, called by spring
	 */
	@Override
	public void buildScreensFlow() {

		addTransitions(mainScreen,
				new ScreenTransition(REQUEST_SELECTION, selectionScreen),
				new ScreenTransition(EXIT, exitScreen));

		addTransitions(exitScreen);

		addScreen(batchIdScreen);
		addScreenTransitions(selectionScreen, new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, batchIdScreen));
		addScreenTransitions(batchIdScreen, new ScreenTransition(BATCH_ID_EXP_DT_REGISTERED,
				mainScreen));
	}

	public void setBatchIdScreen(IScreenGetter batchIdScreen) {
		this.batchIdScreen = batchIdScreen;
	}
}