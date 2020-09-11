package com.sicpa.standard.sasscl.business.production.impl;

import static com.sicpa.standard.gui.utils.ThreadUtils.waitForNextTimeStamp;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Production.ERROR_MAX_SERIALIZATION_ERRORS;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.SENT_TO_REMOTE_SERVER_ERROR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.production.IProduction;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.storage.productPackager.IProductsPackager;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionSendingProgress;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;

public class Production implements IProduction {

	public static final int MAX_RETRY_SEND_PRODUCTION = 3;

	private static final Logger logger = LoggerFactory.getLogger(Production.class);

	private int productionSendBatchSize;
	private int productionDataSerializationErrorThreshold;
	protected SubsystemIdProvider subsystemIdProvider;
	private IStorage storage;
	private IRemoteServer remoteServer;
	// use in case we cannot save to disk, product will be package on fly and
	// then send to the remote server
	private IProductsPackager productsPackager;

	private final List<Product> products = Collections.synchronizedList(new LinkedList<>());
	private volatile boolean cancelSending;
	private long delayBetweenPackageSent = 1000;
	private final Object sendProductionLock = new Object();
	private final Object packageLock = new Object();
	private final Object saveProductionLock = new Object();

	/**
	 * store in memory the product created by the activation
	 */
	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		Product product = evt.getProduct();
		if (product != null) {
			prepareProduct(product);
			products.add(product);
		}
	}

	protected void prepareProduct(Product product) {
		product.setSubsystem(subsystemIdProvider.get());

		if (product.getSku() != null) {
			SKU sku = product.getSku().copySkuForProductionData();
			product.setSku(sku);
		}
	}

	// only used by serilizeProductionData()
	private int errorSaveCounter;

	/**
	 * give to the storage the in memory products and then remove then from the memory
	 */
	@Override
	public void saveProductionData() {
		synchronized (saveProductionLock) {
			if (products.isEmpty()) {
				return;
			}

			List<Product> productsToSave = new ArrayList<>();

			try {
				logger.debug("Production serialization {}", products.size());
				Product product;

				while (!products.isEmpty()) {
					synchronized (products) {
						product = products.remove(0);
					}
					productsToSave.add(product);
					if (enoughtProductInBatchForSerialization(productsToSave)) {
						saveProductsInStorage(productsToSave);
					}
				}
				if (!productsToSave.isEmpty()) {
					saveProductsInStorage(productsToSave);
				}
				errorSaveCounter = 0;
			} catch (Exception e) {
				logger.error("", e);
				handleProductSerializationException(productsToSave, e);
			}
		}
	}

	private void waitForANewFileName() {
		waitForNextTimeStamp();
	}

	private void saveProductsInStorage(List<Product> productsToSave) throws StorageException {
		waitForANewFileName();
		storage.saveProduction(productsToSave.toArray(new Product[productsToSave.size()]));
		productsToSave.clear();
	}

	private boolean enoughtProductInBatchForSerialization(List<Product> productsToSave) {
		return productsToSave.size() > productionSendBatchSize;
	}

	private void handleProductSerializationException(List<Product> productsToSave, Exception e) {
		// if there is an exception when saving the production
		// increase a counter
		// if the counter reach a limit
		// stop the production and try to send to remote server
		errorSaveCounter++;

		if (isTooManySerializationError()) {
			tooManySerializationErrorReached(productsToSave, e);
		} else {
			// restore the product that were not saved
			restoreProdctsNotSavedOnError(productsToSave);
		}
		logger.warn("Error serializing production", e);
	}

	private boolean isTooManySerializationError() {
		return errorSaveCounter >= productionDataSerializationErrorThreshold;
	}

	private void tooManySerializationErrorReached(List<Product> productsToSave, Exception e) {
		EventBusService.post(new MessageEvent(this, ERROR_MAX_SERIALIZATION_ERRORS, e.getMessage()));
		try {
			packageAndSendProductionOnError(productsToSave);
		} catch (Exception e1) {
			logger.warn("Error sending production to remote server after serialization error", e1);
			restoreProdctsNotSavedOnError(productsToSave);
		}
	}

	private void restoreProdctsNotSavedOnError(List<Product> productsToSave) {
		products.addAll(productsToSave);
	}

	private void packageAndSendProductionOnError(List<Product> productsToSave) throws RemoteServerException {
		for (PackagedProducts pack : productsPackager.getPackagedProducts(productsToSave)) {
			remoteServer.sendProductionData(pack);
		}
		productsToSave.clear();
	}

	@Override
	public void onExitSendAllProductionData() {
		setDelayBetweenPackageSent(0);
		sendAllProductionData();
	}

	/**
	 * send the encoder info to the remote server<br>
	 * and then send the production data to the remote server
	 */
	@Override
	public void sendAllProductionData() {

		if (!remoteServer.isConnected()) {
			return;
		}

		synchronized (sendProductionLock) {
			logger.debug("Send production");
			sendAllEncoderInfo();
			sendAllBatchOfProducts();
		}
	}

	private void sendAllEncoderInfo() {
		try {
			List<EncoderInfo> encoderInfos = storage.getAllEndodersInfo();

			if (!encoderInfos.isEmpty()) {
				remoteServer.sendEncoderInfos(encoderInfos);
				storage.notifyEncodersInfoSent(encoderInfos);
			}
		} catch (Exception e) {
			logger.error("error sending encoder infos", e);
		}
	}

	private void sendAllBatchOfProducts() {

		int totalBatchCount = storage.getBatchOfProductsCount();
		AtomicInteger totalProductsCount = new AtomicInteger();

		if (totalBatchCount > 0) {

			AtomicInteger currentIndex = new AtomicInteger();
			PackagedProducts batch;

			int i=1;  
			while (i<=totalBatchCount && (batch = storage.getABatchOfProducts()) != null && !cancelSending) {
				sendABatchOfProducts(batch, totalBatchCount, currentIndex, totalProductsCount);
				i++;
			}

			// Reset cancelSending flag
			cancelSending = false;

			logger.info("Total data sent to remote server {}", totalProductsCount);
			if (totalProductsCount.get() > 0) {
				MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.SENT_TO_REMOTE_SERVER_OK,
						totalProductsCount + ""));
			}
		}
	}

	protected void sendABatchOfProducts(PackagedProducts batch, int totalBatchCount, AtomicInteger currentIndex,
			AtomicInteger productCount) {

		currentIndex.incrementAndGet();
		logger.info("Sending package {}/{}", currentIndex.get(), totalBatchCount);
		EventBusService.post(new ProductionSendingProgress(totalBatchCount, currentIndex.get()));

		boolean tryToSend = true;
		int retriesLeft = MAX_RETRY_SEND_PRODUCTION;
		while (tryToSend) {
			try {
				// highly cpu consuming so wait a bit between each call
				// asynchronous, executed every 10 min, so it doesn't
				// matter if it waits a bit
				// when exiting the delay is set to 0
				ThreadUtils.sleepQuietly(delayBetweenPackageSent);
				sendABatchOfProductsOneTry(batch, productCount);
				tryToSend = false;

			} catch (Exception e) {
				retriesLeft--;
				logger.warn("Error sending production to remote server, retries left: {}", retriesLeft);
				logger.error("", e);

				if (!remoteServer.isConnected()) {
					logger.info("Server disconnected, aborting sending production");
					cancelSending();
					tryToSend = false;
				} else if (retriesLeft == 0) {
					tryToSend = false;
					storage.notifyDataErrorSendingToRemoteServer();
					MonitoringService.addSystemEvent(new BasicSystemEvent(SENT_TO_REMOTE_SERVER_ERROR, productCount
							+ ""));
				}
			}
		}
	}

	private void sendABatchOfProductsOneTry(PackagedProducts batch, AtomicInteger productCount)
			throws RemoteServerException {
		remoteServer.sendProductionData(batch);
		storage.notifyDataSentToRemoteServer();
		productCount.addAndGet(batch.getProducts().size());
	}

	@Override
	public void packageProduction() {
		synchronized (packageLock) {
			logger.debug("Package production");
			storage.packageProduction(productionSendBatchSize);
		}
	}

	public void setProductsPackager(IProductsPackager productsPackager) {
		this.productsPackager = productsPackager;
	}

	@Override
	public void cancelSending() {
		this.cancelSending = true;
	}

	public boolean isCancelSending() {
		return cancelSending;
	}

	public void setDelayBetweenPackageSent(long delayBetweenPackageSent) {
		this.delayBetweenPackageSent = delayBetweenPackageSent;
	}

	public long getDelayBetweenPackageSent() {
		return delayBetweenPackageSent;
	}

	public void setProductionSendBatchSize(int productionSendBatchSize) {
		this.productionSendBatchSize = productionSendBatchSize;
	}

	public void setProductionDataSerializationErrorThreshold(int productionDataSerializationErrorThreshold) {
		this.productionDataSerializationErrorThreshold = productionDataSerializationErrorThreshold;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setRemoteServer(IRemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}
}