package com.sicpa.standard.gui.screen.machine.impl.SPL.mainPanel;

import javax.swing.JPanel;

import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.StatisticsModel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.SystemInfoModel;

public abstract class AbstractSPLMainPanel extends JPanel {

	public AbstractSPLMainPanel() {
	}

	public abstract StatisticsModel getStatsModel();

	public abstract SystemInfoModel getSystemInfoModel();
}
