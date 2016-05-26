package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.utils.ThreadDumpBean;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.monitoring.utils.FileInfo;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RemoteControlSas implements RemoteControlSasMBean {

	private static transient org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteControlSas.class);

	protected SasAppMBean appBean;

	protected Map<String, ISaveRemotlyUpdatedBeanTask> saveBeanTasks;
	protected ISaveRemotlyUpdatedBeanTask defaultSaveTask;

	public RemoteControlSas() {
		saveBeanTasks = new HashMap<String, ISaveRemotlyUpdatedBeanTask>();
		defaultSaveTask = new DefaultSaveRemotelyUpdateBeanTask();

	}

	public void setAppBean(final SasAppMBean sasApp) {
		this.appBean = sasApp;
	}

	public SasAppMBean getAppBean() {
		return this.appBean;
	}

	@Override
	public Map<String, Level> getLoggersList() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Map<String, Level> res = new HashMap<String, Level>();
		for (Logger logger : loggerContext.getLoggerList()) {
			if (logger.getLevel() != null) {
				res.put(logger.getName(), logger.getLevel());
			} else {
				res.put(logger.getName(), null);
			}
		}
		return res;
	}

	@Override
	public void setLoggerLevel(final String loggerName, final Level level) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger logger = loggerContext.getLogger(loggerName);
		logger.setLevel(level);

	}

	@Override
	public String getXMLBean(final String beanName) {
		try {
			Object res = getBean(beanName);
			if (res != null) {
				return getXML(res);
			}
		} catch (Exception e) {

		}
		return null;
	}

	protected String getXML(Object bean) {
		XStream xStream = new XStream();
		return xStream.toXML(bean);
	}

	protected Object getBean(final String beanName) {
		try {
			return BeanProvider.getBean(beanName);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public void saveXMLBean(final String beanName, final String xml) {
		try {
			Object currentObject = getBean(beanName);
			XStream xStream = new XStream();
			Object remotlyModified = xStream.fromXML(xml);

			ISaveRemotlyUpdatedBeanTask task = saveBeanTasks.get(beanName);
			if (task == null) {
				task = defaultSaveTask;
			}
			task.save(beanName, remotlyModified, currentObject);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public List<BasicSystemEvent> getSystemEventList(final Date from, final Date to) {
		return MonitoringService.getSystemEventList(from, to);
	}

	@Override
	public List<IncrementalStatistics> getIncrementalStatistics(final Date from, final Date to) {
		return MonitoringService.getIncrementalStatistics(from, to);
	}

	@Override
	public boolean isRunning() {
		return this.appBean.isRunning();
	}

	@Override
	public String getLastProductScannedDate() {
		return this.appBean.getLastProductScannedDate();
	}

	@Override
	public String getApplicationStatus() {
		return this.appBean.getApplicationStatus();
	}

	@Override
	public String getApplicationLastRunnningStartDate() {
		return this.appBean.getApplicationLastRunnningStartDate();
	}

	@Override
	public String getApplicationLastRunnningStopDate() {
		return this.appBean.getApplicationLastRunnningStopDate();
	}

	@Override
	public long getSubsystem() {
		return this.appBean.getSubsystem();
	}

	@Override
	public String getProductionMode() {
		return this.appBean.getProductionMode();
	}

	@Override
	public String getSKU() {
		return this.appBean.getSKU();
	}

	@Override
	public String getDeviceDisconnected() {
		return this.appBean.getDeviceDisconnected();
	}

	@Override
	public String getStatistics() {
		return this.appBean.getStatistics();
	}

	@Override
	public String getStorageInfo() {
		return this.appBean.getStorageInfo();
	}

	@Override
	public List<FileInfo> getFolderInfo(final String folderName) {
		List<FileInfo> res = new ArrayList<FileInfo>();
		File folder = new File(folderName);
		if (folder.exists() && folder.isDirectory()) {
			for (File f : folder.listFiles()) {
				res.add(new FileInfo(f));
			}
		}
		return res;
	}

	@Override
	public byte[] getFileContent(final String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			try {
				return FileUtils.readFileToByteArray(f);
			} catch (IOException e) {
				logger.error("", e);
				return null;
			}
		} else {
			return null;
		}
	}

	public void addSaveTask(String beanName, ISaveRemotlyUpdatedBeanTask task) {
		saveBeanTasks.put(beanName, task);
	}

	public void setDefaultSaveTask(ISaveRemotlyUpdatedBeanTask defaultSaveTask) {
		this.defaultSaveTask = defaultSaveTask;
	}

	public ISaveRemotlyUpdatedBeanTask getDefaultSaveTask() {
		return defaultSaveTask;
	}

	@Override
	public String getLastSucessfullSynchronisationWithRemoteServerDate() {
		return appBean.getLastSucessfullSynchronisationWithRemoteServerDate();
	}

	@Override
	public String getLastSucessfullSynchronisationWithRemoteServerProduct() {
		return appBean.getLastSucessfullSynchronisationWithRemoteServerProduct();
	}

	@Override
	public String getLastSynchronisationWithRemoteServerDate() {
		return appBean.getLastSynchronisationWithRemoteServerDate();
	}

	@Override
	public String getLastSynchronisationWithRemoteServerStatus() {
		return appBean.getLastSynchronisationWithRemoteServerStatus();
	}

	@Override
	public String getPlcVersion() {
		return appBean.getPlcVersion();
	}

	@Override
	public int getIsInProduction() {
		return appBean.getIsInProduction();
	}

	@Override
	public String createThreadsDump() {
		return ThreadDumpBean.getAllThreadsDump();
	}

	@Override
	public int getNbValidProducts() {
		return appBean.getNbValidProducts();
	}

	@Override
	public int getNbInvalidProducts() {
		return appBean.getNbInvalidProducts();
	}

	@Override
	public String getWarnings() {
		return this.appBean.getWarnings();
	}

	@Override
	public String getErrors() {
		return this.appBean.getErrors();
	}

	@Override
	public String getDeviceCameraStatus() {
		return this.appBean.getDeviceCameraStatus();
	}

	@Override
	public int getDevicePlcStatus() {
		return this.appBean.getDevicePlcStatus();
	}

	@Override
	public int getDeviceMasterStatus() {
		return this.appBean.getDeviceMasterStatus();
	}

	@Override
	public long getLastProductScanned() {
		return this.appBean.getLastProductScanned();
	}

	@Override
	public String getSizeOfPackagedFolder() {
		return this.appBean.getSizeOfPackagedFolder();
	}

	@Override
	public String getSizeOfSentFolder() {
		return this.appBean.getSizeOfSentFolder();
	}

	@Override
	public String getSizeOfBufferFolder() {
		return this.appBean.getSizeOfBufferFolder();
	}

	@Override
	public String getPackagedFolderOldestFileDate() {
		return this.appBean.getPackagedFolderOldestFileDate();
	}

	@Override
	public int getNumberOfQuarantineProductionFile() {
		return this.appBean.getNumberOfQuarantineProductionFile();
	}

	@Override
	public int getOfflineCountingQuantity() {
		return this.appBean.getOfflineCountingQuantity();
	}

	@Override
	public String getOfflineCountingLastStopDate() {
		return this.appBean.getOfflineCountingLastStopDate();
	}

	@Override
	public String getOfflineCountingLastProductionDate() {
		return this.appBean.getOfflineCountingLastProductionDate();
	}

	@Override
	public String getLastSendingNumberOfProducts() {
		return this.appBean.getLastSendingNumberOfProducts();
	}

	@Override
	public String getPlcInfoVars() {
		return appBean.getPlcInfoVars();
	}
}
