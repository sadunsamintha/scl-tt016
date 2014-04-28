package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;
import com.sicpa.standard.sasscl.wizard.pombuilder.resources.ResourcesFactory;

public class CreatePomAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {

			String pom = FileUtils.readFileToString(new File("template/pomTemplate.xml"));
			pom = pom.replace("${projectName}", ProjectContext.getProjectName());
			pom = pom.replace("${applicationType}", ProjectContext.getApplicationType().getPackageName());
			pom = pom.replace("${dependencies}", ProjectContext.createDependency(ProjectContext.getApplicationType())
					.getXML());
			pom = pom.replace("${resources}", ResourcesFactory.create().getXML());

			FileUtils.writeStringToFile(new File(ProjectContext.getProjectPath() + "/pom.xml"), pom);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		} 
	}

}
