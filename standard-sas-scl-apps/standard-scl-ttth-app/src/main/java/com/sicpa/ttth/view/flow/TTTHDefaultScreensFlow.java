package com.sicpa.ttth.view.flow;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.EXIT;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.REQUEST_SELECTION;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.REQUEST_SELECTION_CANCEL;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BARCODE_TRANSITION;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BATCH_ID_TRANSITION;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.STANDARD_MODE_TRANSITION;

/**
 * Overwritting class of DefaultScreensFlow to manage
 *
 */
public class TTTHDefaultScreensFlow extends DefaultScreensFlow {

	protected IScreenGetter batchJobIdScreen;
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

		addScreen(batchJobIdScreen);
		addScreen(barcodeScreen);
		addScreenTransitions(batchJobIdScreen, new ScreenTransition(BATCH_ID_TRANSITION, selectionScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(BATCH_ID_TRANSITION, selectionScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, mainScreen));
		addScreenTransitions(barcodeScreen, new ScreenTransition(BARCODE_TRANSITION, selectionScreen));
		addScreenTransitions(batchJobIdScreen, new ScreenTransition(BARCODE_TRANSITION, barcodeScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(STANDARD_MODE_TRANSITION, batchJobIdScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(BARCODE_TRANSITION, barcodeScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, mainScreen));
		addScreenTransitions(selectionScreen, new ScreenTransition(REQUEST_SELECTION_CANCEL, mainScreen));

	}

	public void setBatchJobIdScreen(IScreenGetter batchJobIdScreen) {
		this.batchJobIdScreen = batchJobIdScreen;
	}

	public void setBarcodeScreen(IScreenGetter barcodeScreen) {
		this.barcodeScreen = barcodeScreen;
	}
}