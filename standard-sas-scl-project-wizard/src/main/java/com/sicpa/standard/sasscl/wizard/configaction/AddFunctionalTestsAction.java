package com.sicpa.standard.sasscl.wizard.configaction;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddFunctionalTestsAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {
		try {
			copyCommon();
			switch (ProjectContext.getApplicationType()) {
			case SAS:
				copySas();
				break;
			case SCL:
				copyScl();
				break;
			}
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
		// TODO remove the CustomisationTest
	}

	protected void copyCommon() throws IOException {
		FileUtils.copyDirectory(new File("template/test-func/common"), new File(ProjectContext.getProjectPath()
				+ "/src/test/java"));
	}

	protected void copySas() throws IOException {
		FileUtils.copyDirectory(new File("template/test-func/sas"), new File(ProjectContext.getProjectPath()
				+ "/src/test/java"));
	}

	protected void copyScl() throws IOException {
		FileUtils.copyDirectory(new File("template/test-func/scl"), new File(ProjectContext.getProjectPath()
				+ "/src/test/java"));
	}
}
