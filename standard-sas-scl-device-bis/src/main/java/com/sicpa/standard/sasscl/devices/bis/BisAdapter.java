package com.sicpa.standard.sasscl.devices.bis;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.skureader.ISkuFinder;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;
import com.sicpa.std.bis2.core.messages.RemoteMessages;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkuMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.Alert.SKU_RECOGNITION_TOO_MANY_UNKNOWN;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.SkuRecognition.UNEXPECTED_SKU_CHANGED;

public class BisAdapter extends AbstractStartableDevice implements IBisAdaptor, IBisControllerListener {

	private static final Logger logger = LoggerFactory.getLogger(BisAdapter.class);

	private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected IBisController controller;
	private ISkuBisProvider skuBisProvider;
	private boolean blockProduction;
	private int unknownSkuId;
	private boolean displayAlertMessage;
	private ISkuFinder skuFinder;

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
			throw new BisAdaptorException("BIS error on connect");
		}
	}

	@Override
	protected void doDisconnect() throws BisAdaptorException {
		try {
			controller.disconnect();
			EventBusService.unregister(this);
		} catch (Exception e) {
			logger.error("", e);
		}
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	protected void doStart() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

	@Override
	protected void doStop() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STOPPED);
	}

	@Override
	public void onConnection() {
		logger.debug("BIS connected");
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		TaskExecutor.execute(this::sendSkusToBis);
	}

	@Override
	public void onDisconnection() {
		logger.debug("BIS disconnected");
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	private void fireAlertEvent(Alert alert) {
		StringBuilder warningMsg = new StringBuilder();
		warningMsg.append("[BIS ");
		warningMsg.append(alert.getSeverity().getValueDescriptor().getName());
		warningMsg.append("] ");
		warningMsg.append(alert.getWhoDesc());
		warningMsg.append(" ");
		warningMsg.append(alert.getWhat());
		warningMsg.append(" on ");
		warningMsg.append(dateformat.format(new Date(alert.getWhen())));

		logger.warn(warningMsg.toString());

		if (displayAlertMessage) {
			EventBusService.post(new MessageEvent(this, MessageEventKey.BIS.BIS_ALERT, warningMsg.toString()));
		}
	}

	@Override
	public void alertReceived(Alert alert) {
		fireAlertEvent(alert);
	}

	@Override
	public void recognitionResultReceived(RecognitionResultMessage result) {
		if (!status.equals(DeviceStatus.STARTED)) {
			logger.info("result received not in started:" + result);
			return;
		}
		TaskExecutor.execute(() -> recognitionResultReceivedInternal(result));
	}

	private void recognitionResultReceivedInternal(RecognitionResultMessage result) {
		if (isResultUnknownSKU(result)) {
			fireSkuNotIdentified();
		} else {
			skuRecognized(result.getConfidence().getId());
		}
	}

	private void skuRecognized(int skuid) {
        Optional<SKU> sku = getSkuFromResult(skuid);

		if (sku.isPresent()) {
            EventBusService.post(new SkuRecognizedEvent(sku.get()));
        } else {
            logger.error("no sku for id:" + skuid);
            fireSkuNotIdentified();
        }
    }

	private void fireSkuNotIdentified() {
		EventBusService.post(new SkuNotRecognizedEvent());
	}

	private Optional<SKU> getSkuFromResult(int skuId) {
		return skuFinder.getSkuFromId(skuId);
	}

	private boolean isResultUnknownSKU(RecognitionResultMessage result) {
		return (result.getConfidence() == null) || (result.getConfidence().getId() == unknownSkuId);
	}

	private void sendSkusToBis() {
		controller.sendDomesticMode();

		Collection<SKU> skus = skuBisProvider.getSkusToSendToBIS();
		if (skus.isEmpty()) {
			return;
		}
		controller.sendSkuList(createSkusMessage(skus));
	}

	private SkusMessage createSkusMessage(Collection<SKU> skus) {
		List<SkuMessage> controllerSkus = new ArrayList<>();
		for (SKU sku : skus) {
			controllerSkus.add(toSkuMessage(sku));
		}
		return RemoteMessages.SkusMessage.newBuilder().addAllSku(controllerSkus).build();
	}

	private SkuMessage toSkuMessage(SKU sku) {
		return RemoteMessages.SkuMessage.newBuilder().setId(sku.getId()).setDescription(sku.getDescription()).build();
	}

	@Subscribe
	public void handleMessageEvent(MessageEvent evt) {
		if (evt.getKey().equals(SKU_RECOGNITION_TOO_MANY_UNKNOWN)) {
			controller.sendUnknownSave();
		} else if (evt.getKey().equals(UNEXPECTED_SKU_CHANGED)) {
			controller.sendAutoSave();
		}
	}

	@Override
	public void sendCredential(String user, String password) {
		logger.info("sending credential to bis - user:" + user);
		controller.sendCredential(user, password);
	}

	@Override
	public boolean isBlockProductionStart() {
		return blockProduction;
	}

	public void setBlockProduction(boolean blockProduction) {
		this.blockProduction = blockProduction;
	}

	public void setUnknownSkuId(int unknownSkuId) {
		this.unknownSkuId = unknownSkuId;
	}

	public void setDisplayAlertMessage(boolean displayAlertMessage) {
		this.displayAlertMessage = displayAlertMessage;
	}

	public void setSkuFinder(ISkuFinder skuFinder) {
		this.skuFinder = skuFinder;
	}

	public void setSkuBisProvider(ISkuBisProvider skuBisProvider) {
		this.skuBisProvider = skuBisProvider;
	}
}
