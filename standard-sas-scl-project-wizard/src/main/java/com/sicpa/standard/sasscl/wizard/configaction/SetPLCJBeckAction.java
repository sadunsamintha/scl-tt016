package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetPLCJBeckAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			ProjectContext.addDependency("com.sicpa.standard.plc", "standard-plc-jbeckplc", "1.2.0");
			File stdplcFile = new File(ProjectContext.getProjectPath() + "/src/main/resources/config/stdPlc.xml");
			String config = FileUtils.readFileToString(stdplcFile);

			config = config.replaceAll("<mIp>.*</mIp>", "<mIp>192.168.1.1.1.1->5.5.25.42.69.100@192.168.1.3</mIp>");
			config = config.replaceAll("<mDriverName>.*</mDriverName>", "<mDriverName>JBeckPlcDriver</mDriverName>");
			
			FileUtils.writeStringToFile(stdplcFile, config);
			//needed because the jbeck 1.2.0 point on a too old standard plc version
			ProjectContext.addDependency("com.sicpa.standard.plc", "standard-plc", "1.1.2");
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}
}
