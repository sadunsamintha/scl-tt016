package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.statemachine.State;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.client.common.utils.ReflectionUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.IPlcJmxInfo;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants;
import com.sicpa.standard.sasscl.provider.impl.AppVersionProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.repository.errors.AppMessage;
import com.sicpa.standard.sasscl.repository.errors.IErrorsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import static com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants.ERROR_BUSINESS_FOLDER;
import static com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants.ERROR_LOAD_FOLDER;

public class SasApp extends NotificationBroadcasterSupport implements SasAppMBean {

	private static final Logger logger = LoggerFactory.getLogger(SasApp.class);
	private static final String NO_CAMERAS = "No Cameras configured";
	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	protected SasAppBeanStatistics stats;
	private IErrorsRepository errorsRepository;
	private IPlcJmxInfo plcJmxInfo;
	protected ProductionConfigProvider productionConfigProvider;
	protected AppVersionProvider appVersionProvider;

	private long sequenceNumber = 0;
	private Map<String, String[]> propertyMap = new HashMap<>();
	private ApplicationFlowState currentState;

	public SasApp() {
		populateMapProperties();
		EventBusService.register(this);
	}

	@Override
	public boolean isRunning() {
		return stats.isRunning();
	}

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

	@Override
	public String getLastProductScannedDate() {
		if (stats.getLastProductScannedTime() != null) {
			return DateUtils.format(DATE_FORMAT, stats.getLastProductScannedTime());
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
			return DateUtils.format(DATE_FORMAT, stats.getStartTime());
		} else {
			return "";
		}
	}

	@Override
	public String getApplicationLastRunnningStopDate() {
		if (stats.getStopTime() != null) {
			return DateUtils.format(DATE_FORMAT, stats.getStopTime());
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
		String returnString = "[";
		if (getDevicePlcStatus() == 2) {
			returnString += "PLC";
		}
		if ((!getDeviceCameraStatus().contains(":1")) && (!getDeviceCameraStatus().equals(NO_CAMERAS))) {
			if (returnString.length() > 1) {
				returnString += " - ";
			}
			returnString += "Camera qc_sas";
		}
		if (getDeviceMasterStatus() == 2) {
			if (returnString.length() > 1) {
				returnString += " - ";
			}
			returnString += "Master";
		}

		returnString += "]";
		return returnString;
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
			if (isValidStats(entry.getKey())) {
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
			if (isInvalidStats(entry.getKey())) {
				totalInvalidProducts += entry.getValue();
			}
		}
		return totalInvalidProducts;
	}

	public void setStats(SasAppBeanStatistics stats) {
		this.stats = stats;
		stats.addPropertyChangeListener(evt -> {

            String[] properties = propertyMap.get(evt.getPropertyName());
            if (properties != null) {
                for (String prop : properties) {
                    Object newValue = getFieldValue(prop);
                    Notification n = new AttributeChangeNotification(SasApp.this, sequenceNumber++, System
                            .currentTimeMillis(), prop + " changed", prop, "Object", null, newValue);
                    sendNotification(n);
                }
            }
        });
	}

	private Object getFieldValue(String property) {
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

	private void populateMapProperties() {
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
			return DateUtils.format(DATE_FORMAT, stats.getLastSucessfulToRemoteServerDate());
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
			return DateUtils.format(DATE_FORMAT, stats.getLastSentToRemoteServerDate());
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
		if (appWarnings == null) {
			return "";
		}

		int warningCount = 1;
		String MARKUP;
		StringBuilder warnings = new StringBuilder();
		for (AppMessage mess : appWarnings) {
			MARKUP = StandardMonitoringMBeanConstants.WARNING_MARKUP_OPEN + warningCount
					+ StandardMonitoringMBeanConstants.MARKUP_CLOSE;
			warnings.append(MARKUP);
			warnings.append(DateUtils.format(DATE_FORMAT, mess.getTime()));
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
			errors.append(DateUtils.format(DATE_FORMAT, mess.getTime()));
			errors.append(" : ");
			errors.append(mess.getCode());
			errors.append(" - ");
			errors.append(mess.getMessage());
			errors.append(MARKUP);
			errorCount++;
		}
		return errors.toString();
	}

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

	private String getSizeOfFolderInByte(String folderPath) {
		File errors = new File(folderPath);
		if (!errors.exists() || !errors.isDirectory()) {
			return "";
		}
		File[] files = errors.listFiles();
		if (files == null || files.length == 0) {
			return "";
		}
		long total = 0;
		for (File file2 : files) {
			total += file2.length();
		}
		return "" + total;
	}

	private String getOldestFileDateOfAFolder(String folderPath) {
		File file = new File(folderPath);

		if (!file.exists() || !file.isDirectory()) {
			return "";
		}
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return "";
		}
		long lastMod = files[0].lastModified();
		for (File test : files) {
			if (test.lastModified() < lastMod) {
				lastMod = test.lastModified();
			}
		}
		return DateUtils.format(DATE_FORMAT, new Date(lastMod));
	}

	@Override
	public String getSizeOfPackagedFolder() {
		return getSizeOfFolderInByte(StandardMonitoringMBeanConstants.PACKAGED_FOLDER);
	}

	@Override
	public String getSizeOfSentFolder() {
		return getSizeOfFolderInByte(StandardMonitoringMBeanConstants.SENT_FOLDER);
	}

	@Override
	public String getSizeOfBufferFolder() {
		return getSizeOfFolderInByte(StandardMonitoringMBeanConstants.BUFFER_FOLDER);
	}

	@Override
	public String getPackagedFolderOldestFileDate() {
		return getOldestFileDateOfAFolder(StandardMonitoringMBeanConstants.PACKAGED_FOLDER);
	}

	@Override
	public int getNumberOfQuarantineProductionFile() {
		List<File> folderList = new ArrayList<>();
		List<String> totalFileList = new ArrayList<>();
		folderList.add(new File(ERROR_LOAD_FOLDER));
		folderList.add(new File(ERROR_BUSINESS_FOLDER));
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
		if (date == null) {
			return "";
		}
		return DateUtils.format(DATE_FORMAT, date);
	}

	@Override
	public String getOfflineCountingLastProductionDate() {
		Date date = stats.getOfflineCountingFromDate();
		if (date == null) {
			return "";
		}
		return DateUtils.format(DATE_FORMAT, date);
	}

	@Override
	public String getLastSendingNumberOfProducts() {
		return stats.getLastSendToRemoteServerNumberOfProduct();
	}

	@Override
	public String getPlcInfoVars() {
		return plcJmxInfo.getPlcInfoVars();
	}

	@Override
	public String getAppVersion() {
		return appVersionProvider.get();
	}

	@Override
	public String getTrilightValues() {
		return plcJmxInfo.getTrilightValues();
	}

	private boolean isProductionState(State state) {
		return stats.isRunning();
	}

	private boolean isValidStats(StatisticsKey key) {
		return key.getDescription().equals(StatisticsKey.GOOD.getDescription());
	}

	private boolean isInvalidStats(StatisticsKey key) {
		return key.getDescription().equals(StatisticsKey.BAD.getDescription());
	}

	public void setPlcJmxInfo(IPlcJmxInfo plcJmxInfo) {
		this.plcJmxInfo = plcJmxInfo;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvider) {
		this.productionConfigProvider = productionConfigProvider;
	}

	public void setAppVersionProvider(AppVersionProvider appVersionProvider) {
		this.appVersionProvider = appVersionProvider;
	}
}
