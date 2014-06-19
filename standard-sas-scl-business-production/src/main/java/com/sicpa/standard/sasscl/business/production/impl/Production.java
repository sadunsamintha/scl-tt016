package com.sicpa.standard.sasscl.business.production.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.production.IProduction;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.storage.productPackager.IProductsPackager;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionSendingProgress;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Production implements IProduction {

	private static final Logger logger = LoggerFactory.getLogger(Production.class);

	// list of product that has been processed by the activation module
	protected final List<Product> products = Collections.synchronizedList(new LinkedList<Product>());;

	protected GlobalBean globalConfig;

	protected IStorage storage;

	protected IRemoteServer remoteServer;

	// use in case we cannot save to disk, product will be package on fly and
	// then send to the remote server
	protected IProductsPackager productsPackager;

	protected volatile boolean cancelSending;

	protected long delayBetweenPackageSent = 1000;

	protected final Object sendProductionLock = new Object();
	protected final Object packageLock = new Object();
	protected final Object saveProductionLock = new Object();

	public Production(final GlobalBean globalConfig, final IStorage storage, final IRemoteServer remoteServer) {
		this.globalConfig = globalConfig;
		this.storage = storage;
		this.remoteServer = remoteServer;
	}

	/**
	 * store in memory the product created by the activation
	 */
	@Subscribe
	public void notifyNewProduct(final NewProductEvent evt) {
		Product product = evt.getProduct();
		if (product != null) {

			product.setSubsystem(globalConfig.getSubsystemId());

			if (product.getSku() != null) {
				SKU sku = product.getSku().copySkuForProductionData();
				product.setSku(sku);
			}
			products.add(product);
		}
	}

	// only used by serilizeProductionData()
	protected int errorSaveCounter;

	/**
	 * give to the storage the in memory products and then remove then from the memory
	 */
	@Override
	public void saveProductionData() {
		synchronized (saveProductionLock) {

			if (products.isEmpty()) {
				return;
			}

			final List<Product> productsToSave = new ArrayList<Product>();

			try {
				logger.debug("Production serialization {}", products.size());
				Product product;

				while (products.size() > 0) {
					synchronized (products) {
						product = products.remove(0);
					}
					productsToSave.add(product);
					// do not save a file that contains more than the send batch size
					if (productsToSave.size() > globalConfig.getProductionSendBatchSize()) {
						storage.saveProduction(productsToSave.toArray(new Product[productsToSave.size()]));
						productsToSave.clear();
						// to have a new file name wait a few ms
						ThreadUtils.waitForNextTimeStamp();
					}
				}

				if (productsToSave.size() > 0) {
					ThreadUtils.waitForNextTimeStamp();
					storage.saveProduction(productsToSave.toArray(new Product[productsToSave.size()]));
				}

				errorSaveCounter = 0;

			} catch (final Exception e) {
				logger.error("", e);
				// if there is an exception when saving the production
				// increase a counter
				// if the counter reach a limit
				// stop the production and try to send to remote server
				errorSaveCounter++;

				if (errorSaveCounter >= globalConfig.getProductionDataSerializationErrorThreshold()) {
					EventBusService.post(new MessageEvent(this,
							MessageEventKey.Production.ERROR_MAX_SERIALIZATION_ERRORS, e.getMessage()));
					try {
						for (PackagedProducts pack : productsPackager.getPackagedProducts(productsToSave)) {
							remoteServer.sendProductionData(pack);
						}

						productsToSave.clear();
					} catch (Exception e1) {
						logger.warn("Error sending production to remote server after serialization error", e1);
						// restore the product that were not saved
						for (Product p : productsToSave) {
							products.add(p);
						}
					}
				} else {
					// restore the product that were not saved
					for (Product p : productsToSave) {
						products.add(p);
					}
				}
				logger.warn("Error serializing production", e);
			}
		}

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
			sendAllBatchOfProducts();
		}
	}

	
	protected void sendAllBatchOfProducts() {

		int totalBatchCount = storage.getBatchOfProductsCount();
		AtomicInteger totalProductsCount = new AtomicInteger();

		if (totalBatchCount > 0) {

			AtomicInteger currentIndex = new AtomicInteger();
			PackagedProducts batch;

			while ((batch = storage.getABatchOfProducts()) != null && !cancelSending) {
				sendABatctOfProducts(batch, totalBatchCount, currentIndex, totalProductsCount);
			}

			// Reset cancelSending flag
			cancelSending = false;

			logger.info("Total data sent to remote server {}", totalProductsCount);
			if(totalProductsCount.get() > 0) {
				MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.SENT_TO_REMOTE_SERVER_OK,
						totalProductsCount + ""));
			}
		}		
	}
	
	
	protected void sendABatctOfProducts(final PackagedProducts batch, int totalBatchCount,
			final AtomicInteger currentIndex, final AtomicInteger productCount) {

		currentIndex.incrementAndGet();
		productCount.addAndGet(batch.getProducts().size());

		logger.info("Sending package {}/{}", currentIndex.get(), totalBatchCount);

		EventBusService.post(new ProductionSendingProgress(totalBatchCount, currentIndex.get()));
		
		int maxRetries = 3;
		while(maxRetries != 0){
			try {
				// highly cpu consuming so wait a bit between each call
				// asynchronous, executed every 10 min, so it doesn't
				// matter if it waits a bit
				// when exiting the delay is set to 0
				ThreadUtils.sleepQuietly(delayBetweenPackageSent);
				
				maxRetries--;
				remoteServer.sendProductionData(batch);
				storage.notifyDataSentToRemoteServer();
				break;
				
			} catch (Exception e) {

				logger.warn("Error sending production to remote server, retries left: {}", maxRetries);
				logger.error("",e);

				// Verify that we are still connected to server
				if(remoteServer.isConnected()){
					remoteServer.lifeCheckTick();
				}
				
				if(!remoteServer.isConnected()){
					logger.info("Server disconnected, aborting sending production");
					cancelSending();
					break;
				}

			}
		}
		
		if(maxRetries == 0){
			productCount.addAndGet(-batch.getProducts().size());
			storage.notifyDataErrorSendingToRemoteServer();
			MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.SENT_TO_REMOTE_SERVER_ERROR,
					productCount + ""));
		} else if (cancelSending) {
			productCount.addAndGet(-batch.getProducts().size());
		}
	}


	/**
	 * delegate this call to storage.packageProduction
	 */
	@Override
	public void packageProduction() {
		synchronized (packageLock) {
			logger.debug("Package production");
			storage.packageProduction(globalConfig.getProductionSendBatchSize());
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
}