package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class CreateMainAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {

		try {
			String main = FileUtils.readFileToString(new File("template/Main.java"));
			main = main.replace("${package}", ProjectContext.getRootPackageName());
			main = main.replace("${applicationName}", ProjectContext.getProjectName() + "\\n"
					+ ProjectContext.getApplicationType().getPackageName());
			main = main.replace("${springConfig}", ProjectContext.getApplicationType().getSpringConfigFile());
			main = main.replace("${customSpringConfig}", ProjectContext.getSpringConfigString());

			FileUtils.writeStringToFile(
					new File(ProjectContext.getProjectPath() + "/src/main/java/" + ProjectContext.getRootPackagePath()
							+ "/Main.java"), main);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
