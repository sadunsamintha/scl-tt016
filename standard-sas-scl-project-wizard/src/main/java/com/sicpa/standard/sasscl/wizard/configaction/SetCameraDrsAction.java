package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

/**
 * 
 *
 */
public class SetCameraDrsAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {

		try {

			ProjectContext.addDependency("com.sicpa.standard.camera", "standard-camera-drs", "0.0.3");

			File stdCameraFile = new File(ProjectContext.getProjectPath() + "/src/main/resources/config/stdCamera.xml");

			String config = "<com.sicpa.standard.camera.controller.model.CameraModel>\n";
			config = config + "<mId>1</mId>\n";
			config = config + "<mDriverName>DrsCameraDriver</mDriverName>\n";
			config = config + "<mPort>3000</mPort>\n";
			config = config + "<mIp>192.168.1.2</mIp>\n";
			config = config + "<mConnectionTimeout>5000</mConnectionTimeout>\n";
			config = config + "<mLifeCheckInterval>1000</mLifeCheckInterval>\n";

			// this is required although it is not used in the DRS driver.
			// without this, the value will be set to null when loading with
			// the configUtil and it causes runtime exception
			config = config + "<mImageRetrievalType>NEVER</mImageRetrievalType>\n";
			config = config + "</com.sicpa.standard.camera.controller.model.CameraModel>";

			FileUtils.writeStringToFile(stdCameraFile, config);

			ProjectContext.addSpringConfigItem("SpringConfig.DESCRIPTORS_CAMERA",
					"descriptors\" + File.separator + \"cameraDescriptors_drs.xml");

			// getting the camera job configuration and make sure it is turned off
			File cameraJobConfigFile = new File(ProjectContext.getProjectPath()
					+ "/src/main/resources/config/cameraJobConfig.xml");

			String cameraJobConfigStr = FileUtils.readFileToString(cameraJobConfigFile);
			cameraJobConfigStr = cameraJobConfigStr.replaceAll("setCameraJob=\"true\"", "setCameraJob=\"false\"");
			FileUtils.writeStringToFile(cameraJobConfigFile, cameraJobConfigStr);

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}
}
