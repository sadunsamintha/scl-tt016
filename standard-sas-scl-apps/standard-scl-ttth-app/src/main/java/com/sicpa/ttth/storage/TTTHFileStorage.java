package com.sicpa.ttth.storage;

import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;


public class TTTHFileStorage extends FileStorage {

	private final static Logger logger = LoggerFactory.getLogger(TTTHFileStorage.class);

	public static final String GLOBAL_PROPERTIES_PATH = "profiles/TTTH-SCL/config/global.properties";

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;

	public static final String FILE_DAILY_BATCH_JOB_STATS = "dailyBatchJobStats.data";
	public static final String FILE_BATCH_JOB_HISTORY = "batchJobHistory.data";

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

	public void saveBatchJobHistory(final BatchJobHistory batchJobHistory) {
		logger.debug("Saving {} {}", "Batch Job History " + batchJobHistory.getClass(), FILE_BATCH_JOB_HISTORY);
		try {
			saveData(batchJobHistory, FILE_BATCH_JOB_HISTORY);
		} catch (Exception e) {
			logger.error("Cannot save daily batch job history", e);
		}
	}

	public void saveDailyBatchJobStats(final DailyBatchJobStatistics stats) {
		logger.debug("Saving {} {}", "Batch Job Statistics " + stats.getBatchJobId(), FILE_DAILY_BATCH_JOB_STATS);
		try {
			saveData(stats, stats.getBatchJobId() + "-" + FILE_DAILY_BATCH_JOB_STATS);
		} catch (Exception e) {
			logger.error("Could not save daily batch job stats", e);
		}
	}

	public void deleteDailyBatchJobStats(final String dailyBatchJob) {
		logger.debug("Deleting {} {}", "Batch Job Statistics " + dailyBatchJob, FILE_DAILY_BATCH_JOB_STATS);
		try {
			remove(dailyBatchJob + "-" + FILE_DAILY_BATCH_JOB_STATS);
		} catch (StorageException e) {
			logger.error("Could not delete daily batch job stats", e);
		}
	}

	public BatchJobHistory getBatchJobHistory() {
		logger.debug("Loading {} {}", "Batch Job History ", FILE_BATCH_JOB_HISTORY);

		try {
			return (BatchJobHistory) loadData(FILE_BATCH_JOB_HISTORY);
		} catch (Exception e) {
			if (e.getCause() instanceof FileNotFoundException) {
				logger.warn("Cannot load batch job history (" + e.getCause().getMessage() + ")");
			} else {
				logger.error("Cannot load load batch job history", e);
			}
			return null;
		}
	}

	public DailyBatchJobStatistics getDailyBatchJobStats(String dailyBatchJobId) {
		logger.debug("Loading {} {}", "Batch Job Statistics", FILE_DAILY_BATCH_JOB_STATS);

		try {
			return (DailyBatchJobStatistics) loadData(dailyBatchJobId + "-" + FILE_DAILY_BATCH_JOB_STATS);
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
