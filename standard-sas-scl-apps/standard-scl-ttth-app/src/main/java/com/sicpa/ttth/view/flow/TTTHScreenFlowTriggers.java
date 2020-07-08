package com.sicpa.ttth.view.flow;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;

/**
 * Overwritting interface to Screen Flow Trigger customized to the new BatchId Window
 *
 */
public interface TTTHScreenFlowTriggers {
	ScreenTransitionTrigger BATCH_ID_TRANSITION = new ScreenTransitionTrigger("BATCH_ID_TRANSITION");
	ScreenTransitionTrigger STANDARD_MODE_TRANSITION = new ScreenTransitionTrigger("STANDARD_MODE_TRANSITION");
	ScreenTransitionTrigger BARCODE_TRANSITION = new ScreenTransitionTrigger("BARCODE_TRANSITION");
}
