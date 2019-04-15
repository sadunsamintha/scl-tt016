package com.sicpa.standard.sasscl.controller.view.flow;

import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.EXIT;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.REQUEST_SELECTION;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;
import com.sicpa.standard.client.common.view.screensflow.ScreensFlow;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultScreensFlow extends ScreensFlow {

	protected IScreenGetter selectionScreen;
	protected IScreenGetter mainScreen;
	protected IScreenGetter exitScreen;

	public DefaultScreensFlow() {

	}

	/**
	 * build the flow, called by spring
	 */
	public void buildScreensFlow() {

		addTransitions(mainScreen,
				new ScreenTransition(REQUEST_SELECTION, selectionScreen),
				new ScreenTransition(EXIT, exitScreen));

		addTransitions(selectionScreen,
				new ScreenTransition(PRODUCTION_PARAMETER_SELECTED, mainScreen),
				new ScreenTransition(EXIT, exitScreen));

		addTransitions(exitScreen);
	}

	@Override
	public void moveToNext(final ScreenTransitionTrigger trigger) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultScreensFlow.super.moveToNext(trigger);
			}
		});
	}

	public void setMainScreen(IScreenGetter mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void setSelectionScreen(IScreenGetter selectionScreen) {
		this.selectionScreen = selectionScreen;
	}

	public void setExitScreen(IScreenGetter exitScreen) {
		this.exitScreen = exitScreen;
	}
}