package com.sicpa.standard.sasscl.controller.scheduling;

import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_PACKAGED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.MaxDownTimeReachedEvent;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class RemoteServerScheduledJobs {
	private static final Logger logger = LoggerFactory.getLogger(RemoteServerScheduledJobs.class);

	private final static int MS_BY_DAY = 1000 * 60 * 60 * 24;

	private int remoteServerMaxDownTime_day;
	protected IStorage storage;
	protected IRemoteServer remoteServer;
	protected SkuListProvider skuListProvider;
	private AuthenticatorProvider authenticatorProvider;
	private IScreensFlow screensFlow;

	private ApplicationFlowState currentApplicationState = ApplicationFlowState.STT_NO_SELECTION;

	public void executeInitialTasks() {
		getAuthenticatorFromRemoteServer();
		getProductionParametersFromRemoteServer();
		getLanguageFileFromRemoteServer();
		checkRemoteServerMaxDownTime();
	}

	/**
	 * Download an authenticator from the remote server and save in the local storage and assign it to the authenticator
	 * provider.
	 */
	public synchronized void getAuthenticatorFromRemoteServer() {
		logger.info("Executing job: Trying to download authenticator");
		if (remoteServer.isConnected()) {
			try {
				IAuthenticator auth = remoteServer.getAuthenticator();
				if (auth != null) {
					storage.saveAuthenticator(auth);
					authenticatorProvider.set(auth);
				}
			} catch (Exception e) {
				logger.error("Failed to get new authenticator", e);
			}
		}
	}

	/**
	 * Download production parameters from the remote server and save them in the local storage and assign them to the
	 * production parameters provider.
	 */
	public synchronized void getProductionParametersFromRemoteServer() {
		logger.info("Executing job: Trying to download production parameters");
		if (remoteServer.isConnected()) {
			try {
				ProductionParameterRootNode node = remoteServer.getTreeProductionParameters();
				if (node != null) {
					storage.saveProductionParameters(node);
					skuListProvider.set(node);

					// Refresh current screen
					if (screensFlow.getCurrentScreen() != null) {
						screensFlow.getCurrentScreen().enter();
					}
				}
			} catch (RemoteServerException e) {
				logger.error("Failed to get new production parameters from the remote server", e);
			} catch (Exception e) {
				logger.error("Failed to refresh new production parameters", e);
			}
		}
	}

	/**
	 * Download language bundles from the remote server and save them as language properties files.
	 */
	public synchronized void getLanguageFileFromRemoteServer() {
		logger.info("Executing job: Trying to download language files");
		if (remoteServer.isConnected()) {
			try {
				Map<String, ? extends ResourceBundle> mapBundles = remoteServer.getLanguageBundles();
				if (mapBundles != null) {
					for (Entry<String, ? extends ResourceBundle> entry : mapBundles.entrySet()) {
						saveLanguage(entry.getKey(), entry.getValue());
					}
				}
			} catch (Exception e) {
				logger.error("Failed to download languages files", e);
			}
		}
	}

	private void saveLanguage(String langKey, ResourceBundle bundle) {

		File file = new File("language/sasscl_" + langKey.toLowerCase() + ".properties");

		try (FileWriter writer = new FileWriter(file)) {

			// load current properties in order to not remove a property that does not exit in the remote
			// server
			Properties currentLanguage = getCurrentLanguageProperties(file);
			Properties prop = new Properties();
			prop.putAll(currentLanguage);

			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String message = bundle.getString(key);
				prop.put(key, message);
			}

			file.getParentFile().mkdirs();
			prop.store(writer, "");
		} catch (Exception e) {
			logger.error("Failed to download and save languages files", e);
		}

	}

	private Properties getCurrentLanguageProperties(File f) {
		Properties p = new Properties();
		try (FileReader reader = new FileReader(f)) {
			p.load(reader);
		} catch (Exception e) {
		}
		return p;
	}

	public void checkRemoteServerMaxDownTime() {
		logger.info("Executing job: Trying to check remote server max downtime");
		if (remoteServerMaxDownTime_day > 0) {
			if (!remoteServer.isConnected()) {
				if (containsFileOlderThanThreshold(getPackageFolder())) {
					fireMaxDownTime(true);
				} else {
					fireMaxDownTime(false);
				}
			} else {
				fireMaxDownTime(false);
			}
		}
	}

	private File getPackageFolder() {
		return new File(((FileStorage) storage).getDataFolder() + "/" + FOLDER_PRODUCTION + "/"
				+ FOLDER_PRODUCTION_PACKAGED);
	}

	private boolean containsFileOlderThanThreshold(File packageFolder) {
		if (packageFolder.exists()) {
			for (File f : packageFolder.listFiles()) {
				long lastModified = f.lastModified();
				if (lastModified <= 0) {
					// getting the last modified time failed some time so make sure to ignore it
					continue;
				}
				long delta = System.currentTimeMillis() - lastModified;
				int days = (int) (delta / MS_BY_DAY);
				if (days >= remoteServerMaxDownTime_day) {
					return true;
				}
			}
		}
		return false;
	}

	public void setRemoteServerMaxDownTime_day(int remoteServerMaxDownTime_day) {
		this.remoteServerMaxDownTime_day = remoteServerMaxDownTime_day;
	}

	private void fireMaxDownTime(final boolean reached) {
		EventBusService.post(new MaxDownTimeReachedEvent(reached));
	}

	// trigger by the scheduler or when the production is started/stopped
	public void sendGlobalMonitoringToolInfo() {
		if (!remoteServer.isConnected()) {
			return;
		}
		TaskExecutor.execute(() -> {
			sendGlobalMonitoringToolInfo(currentApplicationState.equals(STT_STARTED));
		});
	}

	private void sendGlobalMonitoringToolInfo(boolean productionStarted) {
		logger.info("Executing job: Trying to send info to global monitoring tool");
		GlobalMonitoringToolInfo info = new GlobalMonitoringToolInfo();
		info.setProductionStarted(productionStarted);
		try {
			remoteServer.sendInfoToGlobalMonitoringTool(info);
		} catch (Exception e) {
			logger.error("Failed to send information to global monitoring tool", e);
		}
	}

	@Subscribe
	public void handleFlowControlStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentApplicationState = evt.getCurrentState();
		if (currentApplicationState.equals(STT_STARTED) || currentApplicationState.equals(STT_STOPPING)) {
			sendGlobalMonitoringToolInfo();
		}
	}

	public void setScreensFlow(IScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setAuthenticatorProvider(AuthenticatorProvider authenticatorProvider) {
		this.authenticatorProvider = authenticatorProvider;
	}

	public void setRemoteServer(IRemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}
}
