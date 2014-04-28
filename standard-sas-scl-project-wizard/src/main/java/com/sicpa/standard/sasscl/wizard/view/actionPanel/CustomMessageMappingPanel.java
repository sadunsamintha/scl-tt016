package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.AddCustomMessagesMapping;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.CustomisationBox;

public class CustomMessageMappingPanel extends CustomisationBox implements IActionProvider {

	public CustomMessageMappingPanel() {
		text = "Custom messages\nmapping";
		setHelpUrl("http://psdwiki.sicpa-net.ads/display/STD/std+sas-scl+Process+Controller#MessagesListener");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public IConfigAction provide() {
		return new AddCustomMessagesMapping();
	}
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
