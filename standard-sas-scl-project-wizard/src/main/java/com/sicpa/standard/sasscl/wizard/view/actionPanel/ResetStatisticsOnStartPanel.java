package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.AddResetStatisticsOnStartAction;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.BusinessBox;

public class ResetStatisticsOnStartPanel extends BusinessBox implements IActionProvider {

	private static final long serialVersionUID = 1L;

	public ResetStatisticsOnStartPanel() {
		text = "Reset statistics\non start";
	}

	@Override
	public IConfigAction provide() {
		return new AddResetStatisticsOnStartAction();
	}

	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
