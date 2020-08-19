package com.sicpa.ttth.storage;

import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;


public class TTTHFileStorage extends FileStorage {

	private final static Logger logger = LoggerFactory.getLogger(TTTHFileStorage.class);

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;

	public static final String FILE_DAILY_BATCH_JOB = "dailyBatchJob.data";

	private String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	private ISimpleFileStorage storageBehavior;

	public TTTHFileStorage(final String baseFolder, String internalFolder, String quarantineFolder,
                           ISimpleFileStorage storageBehavior) {
		super(baseFolder, internalFolder, quarantineFolder, storageBehavior);

		this.quarantineFolder = quarantineFolder;
		this.dataFolder = baseFolder;
		this.internalFolder = internalFolder;
		this.storageBehavior = storageBehavior;
	}

	public void saveDailyBatchJobStats(final DailyBatchJobStatistics stats) {
		logger.debug("Saving {} {}", "Batch Job Statistics " + stats.getBatchJobId(), FILE_DAILY_BATCH_JOB);
		try {
			saveData(stats, stats.getBatchJobId() + "-" + FILE_DAILY_BATCH_JOB);
		} catch (Exception e) {
			logger.error("Cannot save daily batch job", e);
		}
	}

	public DailyBatchJobStatistics getDailyBatchJobStats(String dailyBatchJobId) {
		logger.debug("Loading {} {}", "Batch Job Statistics", FILE_STATISTICS);

		try {
			return (DailyBatchJobStatistics) loadData(dailyBatchJobId + "-" + FILE_DAILY_BATCH_JOB);
		} catch (Exception e) {
			if (e.getCause() instanceof FileNotFoundException) {
				logger.warn("Cannot load batch job statistics (" + e.getCause().getMessage() + ")");
			} else {
				logger.error("Cannot load load batch job statistics", e);
			}
			return null;
		}
	}
}
