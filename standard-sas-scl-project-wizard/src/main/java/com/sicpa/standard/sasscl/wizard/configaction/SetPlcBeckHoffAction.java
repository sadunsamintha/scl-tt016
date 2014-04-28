package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetPlcBeckHoffAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			ProjectContext.addDependency("com.sicpa.standard.plc", "standard-plc-beckhoff", "1.0.6");

			File stdplcFile = new File(ProjectContext.getProjectPath() + "/src/main/resources/config/stdPlc.xml");
			String config = FileUtils.readFileToString(stdplcFile);

			config = config.replaceAll("<mIp>.*</mIp>", "<mIp>5.5.25.42.69.100</mIp>");
			config = config.replaceAll("<mDriverName>.*</mDriverName>", "<mDriverName>PlcBeckhoffDriver</mDriverName>");

			FileUtils.writeStringToFile(stdplcFile, config);

			File dllFolder = new File(ProjectContext.getProjectPath() + "/dll");
			FileUtils.copyDirectory(new File("plc-beckhoff-dll"), dllFolder);
			for (File f : dllFolder.listFiles()) {
				if (!f.getName().endsWith(".dll")) {
					f.delete();
				}
			}

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}

}
