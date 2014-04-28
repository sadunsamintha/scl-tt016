package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetProjectNameAction implements IConfigAction {

	private String projectName;

	public SetProjectNameAction(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public void configureProject() throws ConfigurationException {
		ProjectContext.setProjectName(projectName);
	}

}
