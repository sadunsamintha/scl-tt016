package com.sicpa.standard.sasscl.functionaltests.storage;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.stream.IntStream;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class CleanUpTaskTest extends AbstractFunctionnalTest {

	static final int START_INDEX_OLD_FILE = 0;
	static final int END_INDEX_OLD_FILE = 10;
	static final int START_INDEX_RECENT_FILE = 20;
	static final int END_INDEX_RECENT_FILE = 30;

	String datapath;

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() throws IOException {
		init();
		createDataFiles();
		runCleanupTask();
		checkDataFiles();
	}

	@Override
	public void init() {
		super.init();
		datapath = storage.getDataFolder() + "/" + FileStorage.FOLDER_PRODUCTION + "/"
				+ FileStorage.FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
		new File(datapath).mkdirs();
	}

	private void checkDataFiles() {

		for (int i = START_INDEX_OLD_FILE; i < END_INDEX_OLD_FILE; i++) {
			File f = new File(datapath + "/" + i);
			assertFalse(f.getAbsolutePath() + " should have been deleted", f.exists());

		}

		for (int i = START_INDEX_RECENT_FILE; i < END_INDEX_RECENT_FILE; i++) {
			File f = new File(datapath + "/" + i);
			assertTrue(f.getAbsolutePath() + " should not have been deleted", f.exists());
		}
	}

	private void runCleanupTask() {
		storage.cleanUpOldSentProduction();
	}

	private void createDataFiles() throws IOException {
		setProductionParameter();
		createSomeOldDataFiles();
		createRecentFile();
	}

	private void createSomeOldDataFiles() throws IOException {

		for (int i = START_INDEX_OLD_FILE; i < END_INDEX_OLD_FILE; i++) {
			File f = new File(datapath + "/" + i);
			f.createNewFile();
			f.setLastModified(getTimeThreshold());
		}
	}

	private long getTimeThreshold() {
		BigInteger timeThreshold = BigInteger.valueOf(System.currentTimeMillis());
		BigInteger day = BigInteger.valueOf(1000L * 60 * 60 * 24);
		BigInteger offset = BigInteger.valueOf(cleanupDateThreshold + 1);
		return timeThreshold.subtract(day.multiply(offset)).longValue();
	}

	private void createRecentFile() throws IOException {
		for (int i = START_INDEX_RECENT_FILE; i < END_INDEX_RECENT_FILE; i++) {
			File f = new File(datapath + "/" + i);
			f.createNewFile();
		}
	}
}
