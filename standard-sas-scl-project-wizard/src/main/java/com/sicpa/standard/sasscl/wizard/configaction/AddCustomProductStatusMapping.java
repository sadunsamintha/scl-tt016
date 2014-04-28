package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddCustomProductStatusMapping implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {

			String customBeans = FileUtils.readFileToString(ProjectContext.getCustomBeansFile());

			// remote server mapping
			String msgMapping = FileUtils.readFileToString(new File("template/CustomRemoteServerProductStatusMapping.java"));
			msgMapping = msgMapping.replace("${package}", ProjectContext.getRootPackageName());

			FileUtils.writeStringToFile(new File(ProjectContext.getProjectPath() + "/src/main/java/" + ProjectContext.getRootPackagePath()+ "/CustomRemoteServerProductStatusMapping.java"), msgMapping);
			customBeans += "\nremoteServerProductStatusMapping=" + ProjectContext.getRootPackageName()+ ".CustomRemoteServerProductStatusMapping";

			// --custom beans
			FileUtils.writeStringToFile(ProjectContext.getCustomBeansFile(), customBeans);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

}
