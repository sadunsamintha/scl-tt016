package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.client.common.utils.ReflectionUtils;
import com.sicpa.standard.gui.state.State;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.IPlcJmxInfo;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;
import com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.repository.errors.AppMessage;
import com.sicpa.standard.sasscl.repository.errors.IErrorsRepository;

public class SasApp extends NotificationBroadcasterSupport implements SasAppMBean {

	private static final Logger logger = LoggerFactory.getLogger(SasApp.class);

	protected SasAppBeanStatistics stats;
	protected static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	protected IErrorsRepository errorsRepository;
	protected IPlcJmxInfo plcJmxInfo;
	protected ProductionConfigProvider productionConfigProvider;

	public SasApp() {
		populateMapProperties();
	}

	@Override
	public boolean isRunning() {
		return stats.isRunning();
	}

	protected ApplicationFlowState currentState;

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentState = evt.getCurrentState();
	}

	@Override
	public int getIsInProduction() {
		return currentState == null ? StandardMonitoringMBeanConstants.UNKNOWb
				: isProductionState(currentState) ? StandardMonitoringMBeanConstants.INPRODUCTION
						: StandardMonitoringMBeanConstants.STOPPED;
	}

	/**
	 * define production state
	 * 
	 * @param state
	 * @return
	 */
	protected boolean isProductionState(final State state) {
		return stats.isRunning();
	}

	@Override
	public String getLastProductScannedDate() {
		if (stats.getLastProductScannedTime() != null) {
			return DateUtils.format(dateFormat, stats.getLastProductScannedTime());
		} else {
			return "";
		}
	}

	@Override
	public long getLastProductScanned() {
		if (stats.getLastProductScannedTime() == null) {
			return 0;
		}
		Calendar lastProductScannedTimeCalendar = Calendar.getInstance();
		lastProductScannedTimeCalendar.setTime(stats.getLastProductScannedTime());
		return Calendar.getInstance().getTimeInMillis() - lastProductScannedTimeCalendar.getTimeInMillis();
	}

	@Override
	public String getApplicationStatus() {
		return stats.getState();
	}

	@Override
	public String getApplicationLastRunnningStartDate() {
		if (stats.getStartTime() != null) {
			return DateUtils.format(dateFormat, stats.getStartTime());
		} else {
			return "";
		}
	}

	@Override
	public String getApplicationLastRunnningStopDate() {
		if (stats.getStopTime() != null) {
			return DateUtils.format(dateFormat, stats.getStopTime());
		} else {
			return "";
		}
	}

	@Override
	public long getSubsystem() {
		return stats.getSubsystemId();
	}

	@Override
	public String getProductionMode() {
		ProductionParameters param = stats.getProductionParameters();
		if (param != null) {
			return param.getProductionMode() + "";
		} else {
			return "";
		}
	}

	@Override
	public String getSKU() {
		ProductionParameters param = stats.getProductionParameters();
		if (param != null) {
			return param.getSku() + "";
		} else {
			return "";
		}
	}

	@Override
	public String getDeviceDisconnected() {
		return stats.getDevicesDisconnected();
	}

	@Override
	public String getStatistics() {
		return stats.getProductsStatistics().getValues().toString();
	}

	@Override
	public int getNbValidProducts() {
		Map<StatisticsKey, Integer> statisticsMap = stats.getProductsStatistics().getValues();
		int totalValidProducts = 0;
		for (Entry<StatisticsKey, Integer> entry : statisticsMap.entrySet()) {
			if (entry.getKey() instanceof StatisticsKeyGood) {
				totalValidProducts += entry.getValue();
			}
		}
		return totalValidProducts;
	}

	@Override
	public int getNbInvalidProducts() {
		Map<StatisticsKey, Integer> statisticsMap = stats.getProductsStatistics().getValues();
		int totalInvalidProducts = 0;
		for (Entry<StatisticsKey, Integer> entry : statisticsMap.entrySet()) {
			if (entry.getKey() instanceof StatisticsKeyBad) {
				totalInvalidProducts += entry.getValue();
			}
		}
		return totalInvalidProducts;
	}

	protected long sequenceNumber = 0;

	public void setStats(final SasAppBeanStatistics stats) {
		this.stats = stats;
		stats.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				String[] properties = propertyMap.get(evt.getPropertyName());
				if (properties != null) {
					for (String prop : properties) {
						Object newValue = getFieldValue(prop);
						Notification n = new AttributeChangeNotification(SasApp.this, sequenceNumber++, System
								.currentTimeMillis(), prop + " changed", prop, "Object", null, newValue);
						sendNotification(n);
					}
				}
			}
		});
	}

	protected Object getFieldValue(final String property) {
		try {
			return ReflectionUtils.getPropertyValue(property, this);
		} catch (Exception e) {
			logger.error("no getter found for:" + property);
		}
		return null;
	}

	@Override
	public String getStorageInfo() {
		return stats.getStorageInfo();
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] { info };
	}

	protected Map<String, String[]> propertyMap = new HashMap<String, String[]>();

	protected void populateMapProperties() {
		propertyMap.put("error", new String[] {});
		propertyMap.put("state", new String[] { "running", "applicationStatus" });
		// propertyMap.put("encodersUsedId", new String[]{});
		propertyMap.put("productionParameters", new String[] { "SKU", "productionMode" });
		propertyMap.put("startTime", new String[] { "applicationLastRunnningStartDate" });
		propertyMap.put("stopTime", new String[] { "applicationLastRunnningStopDate" });
		// propertyMap.put("statisticsValues", new String[] {});
		// propertyMap.put("lastProductScannedTime", new String[] { "lastProductScannedDate" });
		propertyMap.put("lastSentToRemoteServerDate", new String[] { "lastSynchronisationWithRemoteServerDate" });
		propertyMap.put("lastSentToRemoteServerStatus", new String[] { "lastSynchronisationWithRemoteServerStatus" });
		propertyMap.put("lastSucessfulToRemoteServerDate",
				new String[] { "lastSucessfullSynchronisationWithRemoteServerDate" });
		propertyMap.put("lastSucessfulToRemoteServerNumberOfProduct",
				new String[] { "lastSucessfullSynchronisationWithRemoteServerProduct" });
	}

	@Override
	public String getLastSucessfullSynchronisationWithRemoteServerDate() {
		if (stats.getLastSucessfulToRemoteServerDate() != null) {
			return DateUtils.format(dateFormat, stats.getLastSucessfulToRemoteServerDate());
		} else {
			return "";
		}
	}

	@Override
	public String getLastSucessfullSynchronisationWithRemoteServerProduct() {
		return stats.getLastSucessfulToRemoteServerNumberOfProduct();
	}

	@Override
	public String getLastSynchronisationWithRemoteServerDate() {
		if (stats.getLastSentToRemoteServerDate() != null) {
			return DateUtils.format(dateFormat, stats.getLastSentToRemoteServerDate());
		} else {
			return "";
		}
	}

	@Override
	public String getLastSynchronisationWithRemoteServerStatus() {
		return stats.getLastSentToRemoteServerStatus();
	}

	@Override
	public String getPlcVersion() {
		return plcJmxInfo.getPlcVersion();
	}

	public void setErrorsRepository(IErrorsRepository errorsRepository) {
		this.errorsRepository = errorsRepository;
	}

	@Override
	public String getWarnings() {

		Collection<AppMessage> appWarnings = errorsRepository.getApplicationWarnings();
		if (appWarnings == null)
			return "";

		int warningCount = 1;
		String MARKUP;
		final StringBuilder warnings = new StringBuilder();
		for (final AppMessage mess : appWarnings) {
			MARKUP = StandardMonitoringMBeanConstants.WARNING_MARKUP_OPEN + warningCount
					+ StandardMonitoringMBeanConstants.MARKUP_CLOSE;
			warnings.append(MARKUP);
			warnings.append(DateUtils.format(dateFormat, mess.getTime()));
			warnings.append(" : ");
			warnings.append(mess.getCode());
			warnings.append(" - ");
			warnings.append(mess.getMessage());
			warnings.append(MARKUP);
			warningCount++;
		}
		return warnings.toString();
	}

	@Override
	public String getErrors() {

		Collection<AppMessage> appErrors = errorsRepository.getApplicationErrors();
		if (appErrors == null)
			return "";

		int errorCount = 1;
		String MARKUP;
		final StringBuilder errors = new StringBuilder();
		for (final AppMessage mess : appErrors) {
			MARKUP = StandardMonitoringMBeanConstants.ERROR_MARKUP_OPEN + errorCount
					+ StandardMonitoringMBeanConstants.MARKUP_CLOSE;
			errors.append(MARKUP);
			errors.append(DateUtils.format(dateFormat, mess.getTime()));
			errors.append(" : ");
			errors.append(mess.getCode());
			errors.append(" - ");
			errors.append(mess.getMessage());
			errors.append(MARKUP);
			errorCount++;
		}
		return errors.toString();
	}

	/**
	 * convert device status
	 * 
	 * @param status
	 * @return
	 */
	protected int convertDeviceStatus(DeviceStatus status) {
		if (DeviceStatus.CONNECTED.equals(status) || DeviceStatus.CONNECTING.equals(status)
				|| DeviceStatus.STARTED.equals(status) || DeviceStatus.STOPPED.equals(status)) {
			return StandardMonitoringMBeanConstants.CONNECTED;
		} else if (DeviceStatus.DISCONNECTED.equals(status)) {
			return StandardMonitoringMBeanConstants.DISCONNECTED;
		} else {
			return StandardMonitoringMBeanConstants.UNKNOWb;
		}
	}

	protected String getDeviceStatus(Collection<String> devices) {
		String output = "";
		for (String dev : devices) {
			output = StandardMonitoringMBeanConstants.DEVICE_STATUS_DELIMITER;
			output += dev + ":" + convertDeviceStatus(errorsRepository.getDeviceStatus(dev));
		}

		if (!devices.isEmpty()) {
			output = output.substring(1);
		}
		return output;
	}

	@Override
	public String getDeviceCameraStatus() {

		IProductionConfig pc = productionConfigProvider.get();
		if (pc == null) {
			return "";
		}
		Collection<String> cameras = new ArrayList<String>();
		for (CameraConfig cc : pc.getCameraConfigs()) {
			cameras.add(cc.getId());
		}
		return getDeviceStatus(cameras);
	}

	@Override
	public int getDevicePlcStatus() {
		DeviceStatus status = errorsRepository.getPlcStatus();
		return convertDeviceStatus(status);
	}

	@Override
	public int getDeviceMasterStatus() {
		DeviceStatus remoteServerStatus = errorsRepository.getRemoteServerStatus();
		return convertDeviceStatus(remoteServerStatus);
	}

	/**
	 * get the byte size of a passed in folder path
	 * 
	 * @param folderPath
	 * @return
	 */
	protected String getSizeOfFolder(String folderPath) {
		final File errors = new File(folderPath);
		if (!errors.exists() || !errors.isDirectory()) {
			return "";
		}
		final File[] files = errors.listFiles();
		if (files == null || files.length == 0) {
			return "";
		}
		long total = 0;
		for (final File file2 : files) {
			total += file2.length();
		}
		return "" + total;
	}

	/**
	 * get the date time in string of the oldest file in passed in folder
	 * 
	 * @param folderPath
	 * @return
	 */
	protected String getOldestFileDateOfAFolder(String folderPath) {
		final File file = new File(folderPath);

		if (!file.exists() || !file.isDirectory()) {
			return "";
		}
		final File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return "";
		}
		long lastMod = files[0].lastModified();
		for (final File test : files) {
			if (test.lastModified() < lastMod) {
				lastMod = test.lastModified();
			}
		}
		return DateUtils.format(dateFormat, new Date(lastMod));
	}

	@Override
	public String getSizeOfPackagedFolder() {
		return getSizeOfFolder(StandardMonitoringMBeanConstants.PACKAGED_FOLDER);
	}

	@Override
	public String getSizeOfSentFolder() {
		return getSizeOfFolder(StandardMonitoringMBeanConstants.SENT_FOLDER);
	}

	@Override
	public String getSizeOfBufferFolder() {
		return getSizeOfFolder(StandardMonitoringMBeanConstants.BUFFER_FOLDER);
	}

	@Override
	public String getPackagedFolderOldestFileDate() {
		return getOldestFileDateOfAFolder(StandardMonitoringMBeanConstants.PACKAGED_FOLDER);
	}

	@Override
	public int getNumberOfQuarantineProductionFile() {
		List<File> folderList = new ArrayList<File>();
		List<String> totalFileList = new ArrayList<String>();
		folderList.add(new File(StandardMonitoringMBeanConstants.ERROR_LOAD_FOLDER));
		folderList.add(new File(StandardMonitoringMBeanConstants.ERROR_BUSINESS_FOLDER));
		for (File errors : folderList) {
			if (!errors.exists() || !errors.isDirectory()) {
				continue;
			}
			if (errors.list() != null) {
				totalFileList.addAll(Arrays.asList(errors.list()));
			}
		}
		return totalFileList == null ? 0 : totalFileList.size();
	}

	@Override
	public int getOfflineCountingQuantity() {
		return stats.getOfflineCountingQuantity();
	}

	@Override
	public String getOfflineCountingLastStopDate() {
		Date date = stats.getOfflineCountingToDate();
		if (date == null)
			return "";
		return DateUtils.format(dateFormat, date);
	}

	@Override
	public String getOfflineCountingLastProductionDate() {
		Date date = stats.getOfflineCountingFromDate();
		if (date == null)
			return "";
		return DateUtils.format(dateFormat, date);
	}

	@Override
	public String getLastSendingNumberOfProducts() {
		return stats.getLastSendToRemoteServerNumberOfProduct();
	}

	@Override
	public String getPlcInfoVars() {
		return plcJmxInfo.getPlcInfoVars();
	}

	public void setPlcJmxInfo(IPlcJmxInfo plcJmxInfo) {
		this.plcJmxInfo = plcJmxInfo;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvider) {
		this.productionConfigProvider = productionConfigProvider;
	}
}
