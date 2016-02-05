package com.sicpa.standard.sasscl;

import static org.apache.commons.io.FileUtils.cleanDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.sicpa.standard.tools.installer.BasicInstaller;
import com.sicpa.standard.tools.installer.task.InstallTask;
import com.sicpa.standard.tools.installer.utils.ClasspathHacker;
import com.sicpa.standard.tools.installer.utils.FileUtils;

public abstract class AbstractSasSclInstaller extends BasicInstaller {

	// 1/ execute installerMain in client-installer.jar
	// 2/ unzip the file found in update folder
	// 3/ add to classpath all files in the root of the unzipped folder (because we need standard-scl-app-x.x.x.jar as
	// it contains the true installer)
	// 4/ call the installer in the launcher
	// 5*/ add to classpath : the libs folder of the update folder
	// 6*/ setup what should not be backed up / overwritten
	// 7/ find out current + target version
	// 8/ do backup
	// 9*/remove libs
	// 10*/remove launcher file
	// 11/copy files from update
	// 12/execute patch tasks necessary to go from current to target version

	private static final String LIBS = "lib";

	@Override
	protected abstract List<InstallTask> getInstallTasks();

	/**
	 * 
	 * @return the name of the launcher artifact without version number or extension- ie: tt021-scl-app
	 */
	protected abstract String getLauncherArtifactName();

	@Override
	public void install() {
		setupClassPath();
		setupFileFilters();
		super.install();
	}

	private void setupClassPath() {
		File libsDir = getUpdateLibsFolder();
		log("trying to add to the class path:" + libsDir.getAbsolutePath());
		ClasspathHacker.addAllFilesInDirToClassPath(libsDir);
	}

	private File getUpdateLibsFolder() {
		return new File(getUpdateFolderPath() + "/" + LIBS);
	}

	private void setupFileFilters() {
		Collection<String> noOverride = Arrays.asList("config", "configHistory", "data", "/dataSimulator/",
				"/internal/", "/internalSimulator/", "/log/", "/monitoring/", "/simulProductSend/");

		Collection<String> noBackup = Arrays.asList("/log/", "/monitoring/", "/update");

		noOverride.forEach(fileToIgnore -> doNotOverwriteFile(fileToIgnore));
		noBackup.forEach(fileToIgnore -> doNotBackupFile(fileToIgnore));
	}

	private void doNotOverwriteFile(String fileToIgnore) {
		addOverwriteFilters(f -> !f.getAbsolutePath().contains(fileToIgnore));
	}

	private void doNotBackupFile(String fileToIgnore) {
		addBackupFilter(f -> !f.getAbsolutePath().contains(fileToIgnore));
	}

	@Override
	protected void afterBackupBeforeCopyTask() throws IOException {
		removeLibsBeforeUpdate();
		removePreviousLauncherFile();
	}

	private void removeLibsBeforeUpdate() throws IOException {
		if (isFirstTimeInstall()) {
			return;
		}
		File libsFolder = new File(LIBS);
		log("removing:" + libsFolder.getAbsolutePath());
		cleanDirectory(libsFolder);

		if (libsFolder.list() != null) {
			log(libsFolder.list().length);
		}
	}

	private void removePreviousLauncherFile() throws IOException {
		if (isFirstTimeInstall()) {
			return;
		}
		File launcherFile = new File(getLauncherArtifactName() + "-" + currentVersion + ".jar");
		log("deleting:" + launcherFile.getAbsolutePath());
		FileUtils.forceDelete(launcherFile);
	}
}
