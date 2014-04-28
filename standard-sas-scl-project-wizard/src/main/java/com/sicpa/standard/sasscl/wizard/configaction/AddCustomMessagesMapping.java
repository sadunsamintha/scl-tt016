package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddCustomMessagesMapping implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {

			String mappingClass = ProjectContext.getApplicationType().getMessageMappingClass();
			String msgMapping = FileUtils.readFileToString(new File("template/CustomMessagesMapping.java"));
			msgMapping = msgMapping.replace("${package}", ProjectContext.getRootPackageName());
			msgMapping = msgMapping.replace("${MessageMappingClass}", mappingClass);

			FileUtils.writeStringToFile(
					new File(ProjectContext.getProjectPath() + "/src/main/java/" + ProjectContext.getRootPackagePath()
							+ "/CustomMessagesMapping.java"), msgMapping);

			String customBeans = FileUtils.readFileToString(ProjectContext.getCustomBeansFile());
			customBeans += "\nmessagesMapping=" + ProjectContext.getRootPackageName() + ".CustomMessagesMapping";
			FileUtils.writeStringToFile(ProjectContext.getCustomBeansFile(), customBeans);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}

}
