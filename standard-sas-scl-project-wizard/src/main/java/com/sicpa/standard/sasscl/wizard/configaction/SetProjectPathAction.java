package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetProjectPathAction implements IConfigAction {
	private String projectPath;

	public SetProjectPathAction(String projectPath) {
		this.projectPath = projectPath;
	}

	@Override
	public void configureProject() throws ConfigurationException {
		ProjectContext.setProjectPath(projectPath);
	}
}
