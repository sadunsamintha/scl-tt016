package com.sicpa.standard.sasscl.device;

import java.io.File;
import java.io.IOException;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class RemoteServerCheckMaxDownTime extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void testMaxDownTimeReached() throws IOException {
		init();
		createSomeOldDataFiles();

		setProductionParameter();
		executeMaxDownTimeTask();

		runAllTasks();
		checkWarningMessage(MessageEventKey.RemoteServer.MAX_DOWNTIME);
		startProduction();

		runAllTasks();

		checkApplicationStatusCONNECTED();
		exit();
	}

	public void executeMaxDownTimeTask() {
		IRemoteServer remote = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		remote.disconnect();
		RemoteServerScheduledJobs job = BeanProvider.getBean(BeansName.SCHEDULING_REMOTE_SERVER_JOB);
		job.checkRemoteServerMaxDownTime();
	}

	private void createSomeOldDataFiles() throws IOException {

		String path = storage.getDataFolder() + "/" + FileStorage.FOLDER_PRODUCTION + "/"
				+ FileStorage.FOLDER_PRODUCTION_PACKAGED;
		new File(path).mkdirs();

		for (int i = 0; i < 10; i++) {
			File f = new File(path + "/" + i);
			f.createNewFile();
		}

		for (int i = 20; i < 30; i++) {
			File f = new File(path + "/" + i);
			f.createNewFile();
			System.out.println(f.setLastModified(13178839210l));
			System.out.println(f.getAbsolutePath() + " last modified:" + f.lastModified());
		}
	}

}
