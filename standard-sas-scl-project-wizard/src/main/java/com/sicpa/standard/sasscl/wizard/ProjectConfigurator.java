package com.sicpa.standard.sasscl.wizard;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.configaction.AddFunctionalTestsAction;
import com.sicpa.standard.sasscl.wizard.configaction.AddResourcesFolderAction;
import com.sicpa.standard.sasscl.wizard.configaction.ConfigurationException;
import com.sicpa.standard.sasscl.wizard.configaction.CreateBasicProjectFoldersAction;
import com.sicpa.standard.sasscl.wizard.configaction.CreateInstallAction;
import com.sicpa.standard.sasscl.wizard.configaction.CreateMainAction;
import com.sicpa.standard.sasscl.wizard.configaction.CreatePomAction;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class ProjectConfigurator {

	public static void main(String[] args) {

		ProjectContext.setApplicationType(ApplicationType.SAS);
		ProjectContext.setProjectPath("c:/projets/xxxxxxxxxxx");
		ProjectContext.setProjectName("ttxxx");
		try {
			new ProjectConfigurator().configureProject();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected final List<IConfigAction> actions = new ArrayList<IConfigAction>();

	public ProjectConfigurator() {

		actions.add(new CreateBasicProjectFoldersAction());
		actions.add(new AddFunctionalTestsAction());
		actions.add(new AddResourcesFolderAction());

	}

	public void configureProject() throws ConfigurationException {

		for (IConfigAction action : actions) {
			action.configureProject();
		}
		// the following actions depend of previous config action so execute them at the end - need to know the
		// package
		new CreateInstallAction().configureProject();
		new CreatePomAction().configureProject();
		new CreateMainAction().configureProject();

	}

	public void addConfigAction(IConfigAction action) {
		actions.add(action);
	}

	public void addMandatoryConfigAction(IConfigAction action) {
		actions.add(0, action);
	}
}
