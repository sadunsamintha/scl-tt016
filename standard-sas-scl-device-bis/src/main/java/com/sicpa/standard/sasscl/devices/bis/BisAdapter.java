package com.sicpa.standard.sasscl.devices.bis;

import static java.util.Collections.unmodifiableList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.std.bis2.core.messages.RemoteMessages;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkuMessage;

public class BisAdapter extends AbstractStartableDevice implements IBisAdaptor, IBisControllerListener {

	private static final Logger logger = LoggerFactory.getLogger(BisAdapter.class);

	private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private IBisController controller;
	private SkuListProvider skuListProvider;
	private boolean blockProduction;

	public BisAdapter() {
		setName("BIS");
	}

	public void setController(IBisController controller) {
		this.controller = controller;
		this.controller.addListener(this);
	}

	@Override
	protected void doConnect() throws BisAdaptorException {
		try {
			controller.connect();
		} catch (Exception e) {
			EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_DISCONNECTED));
			throw new BisAdaptorException("BIS error on connect");
		}
	}

	@Override
	protected void doDisconnect() throws BisAdaptorException {
		try {
			controller.disconnect();
		} catch (Exception e) {
			logger.error("", e);
		}
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	protected void doStart() throws DeviceException {
		try {
			controller.start();
			fireDeviceStatusChanged(DeviceStatus.STARTED);
		} catch (Exception e) {
			throw new BisAdaptorException("BIS error on start");
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
	public void onConnection() {
		logger.debug("BisAdapter | onConnection() ... ");
		updateControllerSkuList(getAvailableSkus());

		EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_CONNECTED));
	}

	private List<SKU> getAvailableSkus() {
		return unmodifiableList(new ArrayList<>(skuListProvider.getAvailableSKUs()));
	}

	@Override
	public void onDisconnection() {
		logger.debug("BisAdapter | onDisconnection()");
		EventBusService.post(new MessageEvent(MessageEventKey.BIS.BIS_DISCONNECTED));
	}

	public void fireRecognitionResultEvent(RecognitionResultMessage result) {
		if ((result.getConfidence() == null)
				|| (result.getConfidence().getId() == controller.getModel().getUnknownSkuId())) {
			// TODO not recognized
		} else {
			// TODO send recognition result
		}
	}

	private void fireAlertEvent(Alert alert) {
		Calendar alertDatetime = Calendar.getInstance();
		alertDatetime.setTimeInMillis(alert.getWhen());

		StringBuffer warningMsg = new StringBuffer();
		warningMsg.append("[BIS ");
		warningMsg.append(alert.getSeverity().getValueDescriptor().getName());
		warningMsg.append("] ");
		warningMsg.append(alert.getWhoDesc());
		warningMsg.append(" ");
		warningMsg.append(alert.getWhat());
		warningMsg.append(" on ");
		warningMsg.append(dateformat.format(alertDatetime.getTime()));

		logger.warn(warningMsg.toString());

		if (this.controller.getModel().isDisplayAlertMessage()) {
			EventBusService.post(new MessageEvent(this, MessageEventKey.BIS.BIS_ALERT, warningMsg.toString()));
		}
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
	}

	private void updateControllerSkuList(List<SKU> skuList) {
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
		return blockProduction;
	}

	public void setBlockProduction(boolean blockProduction) {
		this.blockProduction = blockProduction;
	}
}
