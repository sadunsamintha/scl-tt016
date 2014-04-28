package com.sicpa.standard.sasscl.wizard.configaction;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddPostPackageAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			ProjectContext.addSpringConfigItem("SpringConfigSCL.POST_PACKAGE", "postPackage.xml");

			ProjectContext.getCustomBeansFile();

			String customBeans = FileUtils.readFileToString(ProjectContext.getCustomBeansFile());
			customBeans += "\npostPackage=com.sicpa.standard.sasscl.business.postPackage.PostPackage";
			customBeans += "\nactivation=com.sicpa.standard.sasscl.business.activation.impl.ActivationWithPostPackage";
			customBeans += "\npostPackageBehavior=com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior";
			FileUtils.writeStringToFile(ProjectContext.getCustomBeansFile(), customBeans);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

}
