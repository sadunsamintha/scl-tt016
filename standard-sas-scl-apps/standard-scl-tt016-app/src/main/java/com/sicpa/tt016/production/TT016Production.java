package com.sicpa.tt016.production;

import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.SENT_TO_REMOTE_SERVER_ERROR;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.business.production.impl.Production;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;
import com.sicpa.tt016.devices.remote.ITT016RemoteServer;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.storage.ITT016Storage;

public class TT016Production extends Production implements ITT016Production {
	
	private static final Logger logger = LoggerFactory.getLogger(TT016Production.class);
	
    private ITT016Storage storage;
    private ITT016RemoteServer remoteServer;
    
    private long delayBetweenSkuGrossNetBatchSent = 1000;
    private final Object sendSkuGrossNetLock = new Object();

    @Override
	public void onExitSendAllProductionData() {
    	setDelayBetweenSkuGrossNetBatchSent(0);
    	sendAllSkuGrossNetData();
    	setDelayBetweenPackageSent(0);
		sendAllProductionData();
	}
    
	@Override
	public void sendAllProductionData() {
		super.setRemoteServer(remoteServer);
		super.setStorage(storage);
		super.sendAllProductionData();
	}
	
	@Override
	public void saveProductionData() {
		super.setRemoteServer(remoteServer);
		super.setStorage(storage);
		super.saveProductionData();
	}
	
    @Override
	public void packageProduction() {
    	super.setRemoteServer(remoteServer);
    	super.setStorage(storage);
    	super.packageProduction();
	}

	@Override
	public void sendAllSkuGrossNetData() {
		if (!remoteServer.isConnected()) {
			return;
		}

		synchronized (sendSkuGrossNetLock) {
			logger.debug("Send SKU Gross Net");
			sendAllBatchOfSkuGrossNetData();
		}
	}
	
	private void sendAllBatchOfSkuGrossNetData() {
		int totalBatchCount = storage.getSkuGrossNetBatchCount();
		AtomicInteger totalSkuGrossNetDataCount = new AtomicInteger();

		if (totalBatchCount > 0) {

			AtomicInteger currentIndex = new AtomicInteger();
			SkuGrossNetProductCounterDTO[] batch;

			int i=1;  
			while (i<=totalBatchCount && (batch = storage.getABatchOfSkuGrossNet()) != null) {
				sendABatchOfSkuGrossNet(batch, totalBatchCount, currentIndex, totalSkuGrossNetDataCount);
				i++;
			}

			logger.info("Total sku gross net sent to remote server {}", totalSkuGrossNetDataCount);
			if (totalSkuGrossNetDataCount.get() > 0) {
				MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.SENT_TO_REMOTE_SERVER_OK,
						totalSkuGrossNetDataCount + ""));
			}
		}
	}
	
	protected void sendABatchOfSkuGrossNet(SkuGrossNetProductCounterDTO[] batch, int totalBatchCount, AtomicInteger currentIndex,
			AtomicInteger totalSkuGrossNetDataCount) {

		currentIndex.incrementAndGet();
		logger.info("Sending sku send {}/{}", currentIndex.get(), totalBatchCount);

		boolean tryToSend = true;
		int retriesLeft = MAX_RETRY_SEND_PRODUCTION;
		while (tryToSend) {
			try {
				// highly cpu consuming so wait a bit between each call
				// asynchronous, executed every 10 min, so it doesn't
				// matter if it waits a bit
				// when exiting the delay is set to 0
				ThreadUtils.sleepQuietly(delayBetweenSkuGrossNetBatchSent);
				sendABatchOfSkuGrossNetOneTry(batch, totalSkuGrossNetDataCount);
				tryToSend = false;

			} catch (Exception e) {
				retriesLeft--;
				logger.warn("Error sending sku gross net to remote server, retries left: {}", retriesLeft);
				logger.error("", e);

				if (!remoteServer.isConnected()) {
					logger.info("Server disconnected, aborting sending sku gross net product count info");
					cancelSending();
					tryToSend = false;
				} else if (retriesLeft == 0) {
					tryToSend = false;
					storage.notifySkuGrossNetDataErrorSendingToRemoteServer();
					MonitoringService.addSystemEvent(new BasicSystemEvent(SENT_TO_REMOTE_SERVER_ERROR, totalSkuGrossNetDataCount
							+ ""));
				}
			}
		}
	}
	
	private void sendABatchOfSkuGrossNetOneTry(SkuGrossNetProductCounterDTO[] batch, AtomicInteger productCount) throws InternalException {
		remoteServer.sendSkuGrossNetProductCounter(batch);
		storage.notifySkuGrossNetDataSentToRemoteServer();
		productCount.addAndGet(batch.length);
	}
	
	public void setStorage(ITT016Storage storage) {
		this.storage = storage;
	}

	public void setRemoteServer(ITT016RemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}

	public void setDelayBetweenSkuGrossNetBatchSent(long delayBetweenSkuGrossNetBatchSent) {
		this.delayBetweenSkuGrossNetBatchSent = delayBetweenSkuGrossNetBatchSent;
	}
	
	public long getDelayBetweenSkuGrossNetBatchSent() {
		return delayBetweenSkuGrossNetBatchSent;
	}

}
