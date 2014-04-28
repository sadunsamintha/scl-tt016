package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.AddOfflineCountingAction;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.BusinessBox;

public class OfflineCountingPanel extends BusinessBox implements IActionProvider {

	private static final long serialVersionUID = 1L;

	public OfflineCountingPanel() {
		text = "Offline counting";
		setHelpUrl("http://psdwiki.sicpa-net.ads/display/STD/std+sas-scl+offline+counting");
	}

	@Override
	public IConfigAction provide() {
		return new AddOfflineCountingAction();
	}
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
