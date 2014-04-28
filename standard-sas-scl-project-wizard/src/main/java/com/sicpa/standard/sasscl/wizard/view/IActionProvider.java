package com.sicpa.standard.sasscl.wizard.view;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;

public interface IActionProvider {
	IConfigAction provide();
	boolean isAvailable(ApplicationType type);
}
