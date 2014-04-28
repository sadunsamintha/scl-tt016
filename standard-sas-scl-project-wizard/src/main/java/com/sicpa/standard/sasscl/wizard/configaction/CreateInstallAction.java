package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class CreateInstallAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {

		try {

			String file = "UpdateInstaller.java";
			String install = FileUtils.readFileToString(new File("template/" + file));
			install = install.replace("${package}", ProjectContext.getRootPackageName());

			FileUtils.writeStringToFile(
					new File(ProjectContext.getProjectPath() + "/src/main/java/" + ProjectContext.getRootPackagePath()
							+ "/" + file), install);

			file = "installPatch.bat";
			String installVersion = "1.0.0";

			String bat = FileUtils.readFileToString(new File("template/" + file));
			bat = bat.replace("${installClass}", ProjectContext.getRootPackageName() + ".UpdateInstaller");
			bat = bat.replace("${cp}", "lib/installer-" + installVersion+".jar");
			FileUtils.writeStringToFile(new File(ProjectContext.getProjectPath() + "/src/main/resources/" + file), bat);
			
			ProjectContext.addDependency("com.sicpa.standard.tools", "installer", installVersion);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
