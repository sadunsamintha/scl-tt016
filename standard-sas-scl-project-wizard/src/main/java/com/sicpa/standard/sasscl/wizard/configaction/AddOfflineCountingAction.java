package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddOfflineCountingAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			ProjectContext.addSpringConfigItem("SpringConfig.OFFLINE_COUNTING", "offlineCounting.xml");
			ProjectContext.addDependency("com.sicpa.standard.sasscl", "business-offline-counting",
					ProjectContext.getVersion());
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}
}
