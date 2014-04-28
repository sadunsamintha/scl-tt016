package com.sicpa.standard.sasscl.view.main;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.view.AbstractViewFlowController;

public class MainPanelGetter extends AbstractViewFlowController {

	protected JComponent statsView;
	protected JComponent selectionDisplayView;
	protected JComponent systemInfoView;
	protected MainPanel mainPanel;

	@Override
	public MainPanel getComponent() {
		if (mainPanel == null) {
			mainPanel = new MainPanel(statsView, selectionDisplayView, systemInfoView);
		}
		return mainPanel;
	}

	public void setStatsView(JComponent statsView) {
		this.statsView = statsView;
	}

	public void setSelectionDisplayView(JComponent selectionDisplayView) {
		this.selectionDisplayView = selectionDisplayView;
	}

	public void setSystemInfoView(JComponent systemInfoView) {
		this.systemInfoView = systemInfoView;
	}
}
