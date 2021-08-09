package com.sicpa.tt016.view;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;

public interface TT016ScreenFlowTriggers {

	ScreenTransitionTrigger STOP_PRODUCTION = new ScreenTransitionTrigger("STOP_PRODUCTION");
	ScreenTransitionTrigger STOP_PRODUCTION_REASON_SELECTED = new ScreenTransitionTrigger
			("STOP_PRODUCTION_REASON_SELECTED");
}
