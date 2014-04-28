package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class CreateBasicProjectFoldersAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath()));

			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath() + "/src/main/java/"
					+ ProjectContext.getRootPackagePath()));
			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath() + "/src/main/resources/config"));
			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath() + "/src/main/resources/language"));
			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath() + "/src/main/resources/spring"));
			FileUtils.forceMkdir(new File(ProjectContext.getProjectPath() + "/src/test/java"));
			FileUtils.writeStringToFile(ProjectContext.getCustomBeansFile(), "#beanName = implementationClass");

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
