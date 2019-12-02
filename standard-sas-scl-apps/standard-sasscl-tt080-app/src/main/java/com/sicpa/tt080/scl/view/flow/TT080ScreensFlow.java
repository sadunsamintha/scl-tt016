package com.sicpa.tt080.scl.view.flow;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.*;
import static com.sicpa.tt080.scl.view.TT080ScreenFlowTriggers.BATCH_ID_REGISTERED;

/**
 * Overwritting class of DefaultScreensFlow to manage
 * the flow  with the new BatchId View
 *
 * @author mjimenez
 *
 */
public class TT080ScreensFlow extends DefaultScreensFlow {

	protected IScreenGetter batchIdScreen;

	public TT080ScreensFlow() {

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
		addScreenTransitions(batchIdScreen, new ScreenTransition(BATCH_ID_REGISTERED,
				mainScreen));
	}

	public void setBatchIdScreen(IScreenGetter batchIdScreen) {
		this.batchIdScreen = batchIdScreen;
	}
}