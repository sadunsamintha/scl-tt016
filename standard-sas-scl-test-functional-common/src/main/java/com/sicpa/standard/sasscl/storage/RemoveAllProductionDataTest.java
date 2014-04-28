package com.sicpa.standard.sasscl.storage;

import java.io.File;
import java.io.IOException;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.ioc.BeansName;

public abstract class RemoveAllProductionDataTest extends AbstractFunctionnalTest {

	FileStorage storage;
	String path;
	File folder;

	public void test() throws IOException {
		init();
		createSomeOldDataFiles();
		runCleanupTask();
		checkSendProductionData();
	}

	@Override
	public void init() {
		super.init();
		storage = BeanProvider.getBean(BeansName.STORAGE);
		path = storage.getDataFolder() + "/" + FileStorage.FOLDER_PRODUCTION + "/"
				+ FileStorage.FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
		folder = new File(path);
		folder.mkdirs();
	}

	private void checkSendProductionData() {

		for (int i = 0; i < 10; i++) {
			File f = new File(path + "/" + i);
			assertFalse(f.getAbsolutePath() + " should have been deleted", f.exists());

		}

		for (int i = 20; i < 30; i++) {
			File f = new File(path + "/" + i);
			assertTrue(f.getAbsolutePath() + " should not have been deleted", f.exists());
		}
	}

	private void runCleanupTask() {
		storage.cleanUpOldSentProduction();
	}

	private void createSomeOldDataFiles() throws IOException {

		for (int i = 0; i < 10; i++) {
			File f = new File(path + "/" + i);
			f.createNewFile();
			f.setLastModified(88888888);
		}

		for (int i = 20; i < 30; i++) {
			File f = new File(path + "/" + i);
			f.createNewFile();
		}
	}
}
