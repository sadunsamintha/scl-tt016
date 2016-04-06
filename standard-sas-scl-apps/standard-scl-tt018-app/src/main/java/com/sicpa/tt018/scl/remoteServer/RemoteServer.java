package com.sicpa.tt018.scl.remoteServer;

import static com.sicpa.standard.sasscl.monitoring.MonitoringService.addSystemEvent;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.client.common.timeout.TimeoutLifeCheck;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.std.common.api.activation.exception.ActivationException;

public abstract class RemoteServer extends AbstractRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);

	protected final Map<ProductStatus, IPackageSender> packageSenders = new HashMap<>();
	protected IStorage storage;
	private RemoteServerLifeChecker lifeChecker;

	protected abstract void initPackageSenders();

	@Override
	protected void doConnect() throws RemoteServerException {
		startLifeChecker();
	}

	@Override
	protected void doDisconnect() throws RemoteServerException {
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	@Timeout
	public final IAuthenticator getAuthenticator() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return doGetAuthenticator();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected abstract IAuthenticator doGetAuthenticator() throws ActivationException;// {

	@Override
	@Timeout
	public final void downloadEncoder(final int batchesQuantity, final CodeType codeType, final int year)
			throws RemoteServerException {
		if (!isConnected()) {
			return;
		}
		try {
			doDownloadEncoder(batchesQuantity, codeType, year);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected abstract void doDownloadEncoder(final int quantity, final CodeType codeType, final int year);

	@Override
	@Timeout
	public final ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return doGetTreeProductionParameters();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected abstract ProductionParameterRootNode doGetTreeProductionParameters();

	@Override
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {

	}

	@Override
	@Timeout
	public final void sendProductionData(PackagedProducts products) throws RemoteServerException {
		if (!isConnected()) {
			throw new RemoteServerException();
		}
		if (products == null || products.getProducts() == null || products.getProducts().isEmpty()) {
			return;
		}
		try {
			addSystemEvent(new BasicSystemEvent(LAST_SENT_TO_REMOTE_SERVER, products.getProducts().size() + ""));
			doSendProductionData(products);
		} catch (Exception e) {
			RemoteServerException ex = new RemoteServerException(e);
			ex.setBusiness(e instanceof ActivationException);
			throw ex;
		}
	}

	public void doSendProductionData(final PackagedProducts products) throws ActivationException {

		if (null == packageSenders.get(products.getProductStatus())) {
			logger.error("No sender found for the package type in file: " + products.getFileName());
			return;
		}
		packageSenders.get(products.getProductStatus()).sendPackage(products);
	}

	protected abstract void processActivatedProducts(final PackagedProducts products) throws ActivationException;

	protected abstract void processCountedProducts(final PackagedProducts products) throws ActivationException;

	protected abstract void checkConnection() throws RemoteServerException;

	private void startLifeChecker() {
		if (lifeChecker != null && !lifeChecker.isAlive()) {
			lifeChecker.start();
		} else {
			logger.warn("Not starting Remote Server Life Checker because not implemented");
		}
	}

	@Override
	@TimeoutLifeCheck
	public void lifeCheckTick() {
		try {
			logger.debug("remote server life check");
			checkConnection();
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			logger.error("", e);
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		return null;
	}

	public void setLifeChecker(RemoteServerLifeChecker lifeChecker) {
		this.lifeChecker = lifeChecker;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	@Override
	public void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {

	}
}
