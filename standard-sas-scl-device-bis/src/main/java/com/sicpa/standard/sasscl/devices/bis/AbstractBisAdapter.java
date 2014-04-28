package com.sicpa.standard.sasscl.devices.bis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.bis.skucheck.IUnreadSkuHandler;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.SkuCode;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;

public abstract class AbstractBisAdapter extends AbstractStartableDevice implements IBisAdaptor,
		IBisControllerListener, IConfigurable<BisConfig, IBisAdaptor> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractBisAdapter.class);

	protected List<IBisListener> bisListeners;
	protected SkuCheckFacadeProvider skuCheckFacadeProvider;
	protected int unknownSkuCount = 0;
	protected DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected IBisController controller;
	protected boolean autoSaveTriggered = false;
	protected IUnreadSkuHandler unreadSkuHandler;

	public AbstractBisAdapter() {
		setName("BIS");
	}

	@Override
	public void setBisListeners(List<IBisListener> listeners) {
		this.bisListeners = listeners;
	}

	public void setController(IBisController controller) {
		this.controller = controller;
		this.controller.addListener(this);
	}

	@Override
	protected abstract void doConnect() throws BisAdaptorException;

	@Override
	protected abstract void doDisconnect() throws BisAdaptorException;

	@Override
	public void start() throws BisAdaptorException {
		try {
			unknownSkuCount = 0;
			super.start();
		} catch (DeviceException e) {
			throw new BisAdaptorException(e);
		}
	}

	@Override
	public void stop() throws BisAdaptorException {
		try {
			super.stop();
		} catch (DeviceException e) {
			throw new BisAdaptorException(e);
		}
	}

	public void fireRecognitionResultEvent(RecognitionResultMessage result) {
		if ((result.getConfidence() == null)
				|| (result.getConfidence().getId() == this.controller.getModel().getUnknownSkuId())
				|| (!StringUtils.isEmpty(result.getConfidence().getDescription()) && (result.getConfidence()
						.getDescription().equalsIgnoreCase(this.controller.getModel().getUnknownSkuDescription())))) {
			skuCheckFacadeProvider.get().addUnread();
			unreadSkuHandler.addUnread();
			unknownSkuCount++;
		} else {
			skuCheckFacadeProvider.get().addSku(new SkuCode(String.valueOf(result.getConfidence().getId())));
			unreadSkuHandler.addRead();
			unknownSkuCount = 0;
			autoSaveTriggered = false;
		}

		skuCheckFacadeProvider.get().querySkus();
		
		if (unreadSkuHandler.isThresholdReached()) {
			EventBusService.post(new MessageEvent(this, MessageEventKey.BIS.BIS_UNKNOWN_SKU_EXCEED_THRESHOLD));
		}
	}

	public void fireAlertEvent(Alert alert) {
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

	protected abstract void updateControllerSkuList(List<SKU> skuList);

	public void setSkuCheckFacadeProvider(SkuCheckFacadeProvider skuCheckFacadeProvider) {
		this.skuCheckFacadeProvider = skuCheckFacadeProvider;
	}

	public void setUnreadSkuHandler(IUnreadSkuHandler unreadSkuHandler) {
		this.unreadSkuHandler = unreadSkuHandler;
	}
	
}
