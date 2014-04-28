package com.sicpa.standard.sasscl.view;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;

public interface ScreensFlowTriggers {

	public static final ScreenTransitionTrigger PRODUCTION_PARAMETER_SELECTED = new ScreenTransitionTrigger(
			"SKU_TYPE_SELECTED");
	public static final ScreenTransitionTrigger REQUEST_SELECTION = new ScreenTransitionTrigger("SKU_TYPE_SELECTED");
	public static final ScreenTransitionTrigger EXIT = new ScreenTransitionTrigger("EXIT");

}
