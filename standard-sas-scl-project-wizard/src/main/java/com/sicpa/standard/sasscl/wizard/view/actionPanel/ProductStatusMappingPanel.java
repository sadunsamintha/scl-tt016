package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.AddCustomProductStatusMapping;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.CustomisationBox;

public class ProductStatusMappingPanel extends CustomisationBox implements IActionProvider {

	private static final long serialVersionUID = 1L;

	public ProductStatusMappingPanel() {
		text = "Product status\nmapping";
		setHelpUrl("http://psdwiki.sicpa-net.ads/display/STD/std+sas-scl+products+status+mapping");
	}

	@Override
	public IConfigAction provide() {
		return new AddCustomProductStatusMapping();
	}
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
