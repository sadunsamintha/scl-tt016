package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.AddBarcodeScanningSystemAction;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.DeviceBox;

public class BarcodeScanningSystemPanel extends DeviceBox implements IActionProvider {

	private static final long serialVersionUID = -6890011953043553616L;

	public BarcodeScanningSystemPanel() {
		text = "BRS";
	}

	@Override
	public IConfigAction provide() {
		return new AddBarcodeScanningSystemAction();
	}

	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}

}
