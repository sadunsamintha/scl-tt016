package com.sicpa.standard.sasscl.installer;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.AbstractSasSclInstaller;
import com.sicpa.standard.tools.installer.task.InstallTask;

public class SclAppInstaller extends AbstractSasSclInstaller {

	@Override
	protected List<InstallTask> getInstallTasks() {
		List<InstallTask> tasks = new ArrayList<InstallTask>();
		return tasks;
	}

	@Override
	protected String getLauncherArtifactName() {
		return "scl-app";
	}

}
