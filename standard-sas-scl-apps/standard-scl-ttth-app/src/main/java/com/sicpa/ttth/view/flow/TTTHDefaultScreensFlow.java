package com.sicpa.ttth.view.flow;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.EXIT;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.REQUEST_SELECTION;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BARCODE_TRANSITION;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BATCH_ID_TRANSITION;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.STANDARD_MODE_TRANSITION;

/**
 * Overwritting class of DefaultScreensFlow to manage
 *
 */
public class TTTHDefaultScreensFlow extends DefaultScreensFlow {

	protected IScreenGetter batchIdScreen;
	protected IScreenGetter barcodeScreen;

	public TTTHDefaultScreensFlow() {

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
		addScreen(barcodeScreen);
		addScreenTransitions(batchIdScreen, new ScreenTransition(BATCH_ID_TRANSITION, selectionScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(BATCH_ID_TRANSITION, selectionScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, mainScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(BARCODE_TRANSITION, selectionScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(STANDARD_MODE_TRANSITION, batchIdScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(BARCODE_TRANSITION, barcodeScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, mainScreen));

	}

	public void setBatchIdScreen(IScreenGetter batchIdScreen) {
		this.batchIdScreen = batchIdScreen;
	}

	public void setBarcodeScreen(IScreenGetter barcodeScreen) {
		this.barcodeScreen = barcodeScreen;
	}
}