package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddResourcesFolderAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			FileUtils.copyDirectory(new File("template/resources"), new File(ProjectContext.getProjectPath()
					+ "/src/main/resources"));
			FileUtils.copyFile(new File("template/assembly.xml"), new File(ProjectContext.getProjectPath()
					+ "/assembly.xml"));

			if (ProjectContext.getApplicationType() == ApplicationType.SAS) {
				FileUtils.forceDelete(new File(ProjectContext.getProjectPath()
						+ "/src/main/resources/config/stdPrinter.xml"));
				FileUtils.forceDelete(new File(ProjectContext.getProjectPath()
						+ "/src/main/resources/config/printerSimulator.xml"));
				{
					// make sure the camera simulator will generate code in SAS
					File cameraSimulator = new File(ProjectContext.getProjectPath()
							+ "/src/main/resources/config/cameraSimulator.xml");
					String cameraSimulatorConfig = FileUtils.readFileToString(cameraSimulator);
					cameraSimulatorConfig = cameraSimulatorConfig.replaceAll("<codeGetMethod>.*</codeGetMethod>",
							"<codeGetMethod>generated</codeGetMethod>");
					FileUtils.writeStringToFile(cameraSimulator, cameraSimulatorConfig);
				}
				{
					// global config
					File globalFile = new File(ProjectContext.getProjectPath()
							+ "/src/main/resources/config/global.xml");
					String globalContent = FileUtils.readFileToString(globalFile);
					globalContent = globalContent.replaceAll("<minEncodersThreshold>.*</minEncodersThreshold>", "");
					globalContent = globalContent.replaceAll("<requestNumberEncoders>.*</requestNumberEncoders>", "");
					globalContent = globalContent.replaceAll("<printerAlertThreshold>.*</printerAlertThreshold>", "");
					globalContent = globalContent.replaceAll("<printerAlertInterval_ms>.*</printerAlertInterval_ms>",
							"");
					globalContent = globalContent.replaceAll("<printerAlertLength>.*</printerAlertLength>", "");
					FileUtils.writeStringToFile(globalFile, globalContent);
				}
			}

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
