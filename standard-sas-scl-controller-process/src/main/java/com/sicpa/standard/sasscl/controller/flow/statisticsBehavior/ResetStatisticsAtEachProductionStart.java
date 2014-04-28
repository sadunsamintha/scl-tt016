package com.sicpa.standard.sasscl.controller.flow.statisticsBehavior;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

/**
 * reset the statistics when the production is starting
 * 
 * @author DIelsch
 */
public class ResetStatisticsAtEachProductionStart {

	protected IStatistics statistics;

	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)
				|| evt.getCurrentState().equals(ApplicationFlowState.STT_NO_SELECTION)) {
			statistics.reset();
		}
	}

	public void setStatistics(final IStatistics statistics) {
		this.statistics = statistics;
	}
}
