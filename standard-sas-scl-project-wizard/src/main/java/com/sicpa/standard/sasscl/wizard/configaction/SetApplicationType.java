package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetApplicationType implements IConfigAction {

	private ApplicationType type;

	public SetApplicationType(ApplicationType type) {
		this.type = type;
	}

	@Override
	public void configureProject() throws ConfigurationException {
		ProjectContext.setApplicationType(type);

	}

}
