package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.AddPostPackageAction;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.BusinessBox;

public class PostPackagePanel extends BusinessBox implements IActionProvider {

	private static final long serialVersionUID = 1L;

	public PostPackagePanel() {
		text = "Post package\n(bad codes tracking)";
		setHelpUrl("http://psdwiki.sicpa-net.ads/display/STD/std+sas-scl+Post+package+-+bad+code+tracking");
	}

	@Override
	public IConfigAction provide() {
		return new AddPostPackageAction();
	}

	@Override
	public boolean isAvailable(ApplicationType type) {
		return type==ApplicationType.SCL;
	}
	
}
