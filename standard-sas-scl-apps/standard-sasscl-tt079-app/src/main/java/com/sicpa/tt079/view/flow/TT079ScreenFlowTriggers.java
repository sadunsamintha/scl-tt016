package com.sicpa.tt079.view.flow;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;

/**
 * Overwritting interface to Screen Flow Trigger customized to the new BatchId Window
 *
 */
public interface TT079ScreenFlowTriggers {
	ScreenTransitionTrigger BATCH_ID_EXP_DT_REGISTERED = new ScreenTransitionTrigger("BATCH_ID_EXP_DT_REGISTERED");
	ScreenTransitionTrigger BACK_TO_SELECTION = new ScreenTransitionTrigger("BACK_TO_SELECTION");
}
