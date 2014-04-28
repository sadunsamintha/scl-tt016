package com.sicpa.standard.sasscl.devices.bis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationFailedException;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.std.bis2.core.messages.RemoteMessages;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkuMessage;

public class BisAdapter extends AbstractBisAdapter {

	private static final Logger logger = LoggerFactory.getLogger(BisAdapter.class);

	public BisAdapter() {
		super();
	}

	public BisAdapter(SkuCheckFacadeProvider skuCheckFacadeProvider) {
		super();
		this.skuCheckFacadeProvider = skuCheckFacadeProvider;
	}

	@Override
	protected void doConnect() throws BisAdaptorException {
		try{
			controller.connect();
		}catch (Exception e) {
			EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_DISCONNECTED));
			throw new BisAdaptorException("BIS error on connect");
		}finally{
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		}
	}

	@Override
	protected void doStop() throws DeviceException {
		try {
			controller.stop();
		} catch (Exception e) {
			throw new BisAdaptorException("BIS error on stop");
		} finally {
			fireDeviceStatusChanged(DeviceStatus.STOPPED);
		}
	}

	@Override
	protected void doStart() throws DeviceException {
		try {
			controller.start();
		} catch (Exception e) {
			throw new BisAdaptorException("BIS error on start");
		}finally {
			fireDeviceStatusChanged(DeviceStatus.STARTED);
		}
		
	}

	@Override
	protected void doDisconnect() throws BisAdaptorException {
		try {
			controller.disconnect();
		} catch (Exception e) {
			throw new BisAdaptorException("BIS error on disconnect");
		} finally {
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public void onConnection() {
		logger.debug("BisAdapter | onConnection() ... ");
		updateControllerSkuList(skuCheckFacadeProvider.get().getKnownSkus());

		EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_CONNECTED));
	}

	@Override
	public void onDisconnection() {
		logger.debug("BisAdapter | onDisconnection() ...");
		EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_DISCONNECTED));
	}

	@Override
	public void lifeCheckReceived(LifeCheck lifeCheckResponse) {
		// do nothing
	}

	@Override
	public void alertReceived(Alert alert) {
		fireAlertEvent(alert);
	}

	@Override
	public void recognitionResultReceived(RecognitionResultMessage result) {
		fireRecognitionResultEvent(result);

		if ((this.controller.getModel().getUnknownSkuThreshold() != 0)
				&& (this.unknownSkuCount >= this.controller.getModel().getUnknownSkuThreshold())) {
			EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_UNKNOWN_SKU));

			if (!autoSaveTriggered){
				controller.sendAutoSave();
				autoSaveTriggered = true;
			}
		}
	}

	@Override
	protected void updateControllerSkuList(List<SKU> skuList) {
		if (!CollectionUtils.isEmpty(skuList)) {
			List<SkuMessage> controllerSkus = new ArrayList<SkuMessage>();
			for (SKU sku : skuList) {
				controllerSkus.add(RemoteMessages.SkuMessage.newBuilder().setId(sku.getId())
						.setDescription(sku.getDescription()).build());
			}

			controller.sendSkuList(RemoteMessages.SkusMessage.newBuilder().addAllSku(controllerSkus).build());
		}

	}

	@Override
	public void onLifeCheckFailed() {
		EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_DISCONNECTED));
	}

	@Override
	public void otherMessageReceived(Object result) {
		// ignore other messages
	}

	@Override
	public boolean isBlockProductionStart() {
		return false;
	}

	@Override
	public IConfigurator<BisConfig, IBisAdaptor> getConfigurator() {
		return new IConfigurator<BisConfig, IBisAdaptor>() {
			@Override
			public void execute(BisConfig config, IBisAdaptor configurable) throws ConfigurationFailedException {
				configure(config);
			}
		};
	}

	protected void configure(BisConfig config) throws ConfigurationFailedException {

	}

}
