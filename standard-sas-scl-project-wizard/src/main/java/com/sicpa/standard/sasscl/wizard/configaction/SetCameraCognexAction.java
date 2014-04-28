package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class SetCameraCognexAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {

		try {

			ProjectContext.addDependency("com.sicpa.standard.camera", "standard-camera-cognex","1.2.1");

			File stdCameraFile = new File(ProjectContext.getProjectPath() + "/src/main/resources/config/stdCamera.xml");
			String config = FileUtils.readFileToString(stdCameraFile);

			config = config
					.replaceAll("<mDriverName>.*</mDriverName>", "<mDriverName>CognexCameraDriver</mDriverName>");

			FileUtils.writeStringToFile(stdCameraFile, config);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}

}
