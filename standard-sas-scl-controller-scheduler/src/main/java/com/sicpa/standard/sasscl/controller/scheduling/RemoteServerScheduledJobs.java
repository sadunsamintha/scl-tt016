package com.sicpa.standard.sasscl.controller.scheduling;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
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

	protected int remoteServerMaxDownTime_day;
	protected IStorage storage;
	protected IRemoteServer remoteServer;
	protected SkuListProvider skuListProvider;
	protected AuthenticatorProvider authenticatorProvider;

	public RemoteServerScheduledJobs(final IStorage storage, final IRemoteServer remoteServer,
			final SkuListProvider skuList, final AuthenticatorProvider authenticatorProvider) {
		this.storage = storage;
		this.remoteServer = remoteServer;
		this.skuListProvider = skuList;
		this.authenticatorProvider = authenticatorProvider;
	}

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
					IScreensFlow screensFlow = BeanProvider.getBean("screensFlow");
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
			FileWriter writer = null;
			try {
				Map<String, ? extends ResourceBundle> mapBundles = remoteServer.getLanguageBundles();
				if (mapBundles != null) {
					// for each language
					for (Entry<String, ? extends ResourceBundle> entry : mapBundles.entrySet()) {
						// for each key
						File file = new File("language/sasscl_" + entry.getKey().toLowerCase() + ".properties");
						// load current properties in order to not remove a property that does not exit in the remote
						// server
						Properties currentLanguage = getCurrentLanguageProperties(file);
						Properties prop = new Properties();
						prop.putAll(currentLanguage);

						Enumeration<String> keys = entry.getValue().getKeys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement();
							String message = entry.getValue().getString(key);
							prop.put(key, message);
						}

						file.getParentFile().mkdirs();
						writer = new FileWriter(file);
						prop.store(writer, "");
					}
				}
			} catch (Exception e) {
				logger.error("Failed to download and save languages files", e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param f
	 *            the language properties file
	 * @return the properties loaded from the given file
	 */
	protected Properties getCurrentLanguageProperties(final File f) {
		Properties p = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(f);
			p.load(reader);
		} catch (Exception e) {
		}
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
		}
		return p;
	}

	public void checkRemoteServerMaxDownTime() {
		logger.info("Executing job: Trying to check remote server max downtime");
		if (remoteServerMaxDownTime_day > 0) {
			if (!remoteServer.isConnected()) {
				// check if some file in the package folder is older than the max down time
				// if that's the case the max down time is reached
				if (storage instanceof FileStorage) {
					File packageFolder = new File(((FileStorage) storage).getDataFolder() + File.separator
							+ FileStorage.FOLDER_PRODUCTION + File.separator + FileStorage.FOLDER_PRODUCTION_PACKAGED);

					int miliByDay = 1000 * 60 * 60 * 24;
					if (packageFolder.exists()) {
						for (File f : packageFolder.listFiles()) {
							long lastModified = f.lastModified();
							if (lastModified <= 0) {
								// getting the last modified time failed some time so make sure to ignore it
								continue;
							}
							long delta = System.currentTimeMillis() - lastModified;
							int days = (int) (delta / miliByDay);
							if (days >= remoteServerMaxDownTime_day) {
								fireMaxDownTime(true);
								return;
							}
						}
					}
					fireMaxDownTime(false);
				} else {
					throw new UnsupportedOperationException(
							"Can only check max remote server down time when using an AbstractFileStorage");
				}
			} else {
				fireMaxDownTime(false);
			}
		}
	}

	public void setRemoteServerMaxDownTime_day(int remoteServerMaxDownTime_day) {
		this.remoteServerMaxDownTime_day = remoteServerMaxDownTime_day;
	}

	protected void fireMaxDownTime(final boolean reached) {
		EventBusService.post(new MaxDownTimeReachedEvent(reached));
	}

	// trigger by the scheduler or when the production is started/stopped
	public void sendGlobalMonitoringToolInfo() {
		logger.info("Executing job: Trying to send info to global monitoring tool");
		GlobalMonitoringToolInfo info = new GlobalMonitoringToolInfo();
		info.setProductionStarted(currentApplicationState.equals(ApplicationFlowState.STT_STARTED));
		remoteServer.sentInfoToGlobalMonitoringTool(info);
	}

	protected ApplicationFlowState currentApplicationState = ApplicationFlowState.STT_NO_SELECTION;

	@Subscribe
	public void handleFlowControlStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentApplicationState = evt.getCurrentState();
		if (currentApplicationState.equals(ApplicationFlowState.STT_STARTED)
				|| currentApplicationState.equals(ApplicationFlowState.STT_STOPPING)) {
			sendGlobalMonitoringToolInfo();
		}
	}
}
