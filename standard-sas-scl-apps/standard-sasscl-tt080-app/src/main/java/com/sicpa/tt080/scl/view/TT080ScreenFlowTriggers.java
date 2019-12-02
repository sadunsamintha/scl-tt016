package com.sicpa.tt080.scl.view;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;

/**
 * Overwritting interface to Screen Flow Trigger customized to the new BatchId Window
 *
 * @author mjimenez
 *
 */
public interface TT080ScreenFlowTriggers {
	ScreenTransitionTrigger BATCH_ID_REGISTERED = new ScreenTransitionTrigger("BATCH_ID_REGISTERED");
	ScreenTransitionTrigger PRODUCTION_PARAMETER_TO_BATCH = new ScreenTransitionTrigger("SKU_TYPE_SELECTED_TO_BATCH");
}
