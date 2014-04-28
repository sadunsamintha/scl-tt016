package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddResetStatisticsOnStartAction implements IConfigAction {
	
	@Override
	public void configureProject() throws ConfigurationException {
		ProjectContext.addSpringConfigItem("\"RESET_STATS_ON_START\"", "statistics-resetAtEachStartBehavior.xml");
	}
}
