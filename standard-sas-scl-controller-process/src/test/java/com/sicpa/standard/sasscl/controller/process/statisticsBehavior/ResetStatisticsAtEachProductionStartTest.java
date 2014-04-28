package com.sicpa.standard.sasscl.controller.process.statisticsBehavior;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.statisticsBehavior.ResetStatisticsAtEachProductionStart;

public class ResetStatisticsAtEachProductionStartTest {

	private IStatistics statistics;

	@Before
	public void setup() {
		this.statistics = Mockito.mock(IStatistics.class);
	}

	@Test
	public void testOnProcessStateChanged() {
		ResetStatisticsAtEachProductionStart statBehavior = new ResetStatisticsAtEachProductionStart();
		statBehavior.setStatistics(this.statistics);
		statBehavior.processStateChanged(new ApplicationFlowStateChangedEvent(null, ApplicationFlowState.STT_STARTING,
				""));
		Mockito.verify(this.statistics, Mockito.times(1)).reset();

		statBehavior.processStateChanged(new ApplicationFlowStateChangedEvent(null,
				ApplicationFlowState.STT_NO_SELECTION, ""));
		Mockito.verify(this.statistics, Mockito.times(2)).reset();
	}
}
