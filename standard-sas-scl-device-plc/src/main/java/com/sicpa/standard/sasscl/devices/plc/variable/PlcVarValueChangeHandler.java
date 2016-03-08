package com.sicpa.standard.sasscl.devices.plc.variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.event.PlcVarValueChangeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcVarValueChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlcVarValueChangeHandler.class);

	private PlcProvider plcProvider;
	private IPlcParamSender paramSender;
	private IPlcValuesLoader loader;

	@Subscribe
	public void handlePlcVarValueChangeEvent(PlcVarValueChangeEvent evt) {
		saveAndSendValueToPlc(evt.getLogicVarName(), evt.getValue(), evt.getLineIndex());
	}

	private void saveAndSendValueToPlc(String varName, String value, int lineIndex) {
		saveValue(varName, value, lineIndex);
		if (shouldBeSentToPlc()) {
			try {
				paramSender.sendToPlc(varName, value, lineIndex);
				plcProvider.get().executeRequest(PlcRequest.RELOAD_PLC_PARAM);
			} catch (PlcAdaptorException e) {
				logger.error("", e);
				EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, e, varName));
			}
		}
	}

	private void saveValue(String varName, String value, int lineIndex) {
		if (lineIndex > 0) {
			loader.saveLineNewValue(varName, value, lineIndex);
		} else {
			loader.saveCabinetNewValue(varName, value);
		}
	}

	private boolean shouldBeSentToPlc() {
		IPlcAdaptor plc = plcProvider != null ? plcProvider.get() : null;
		if (plc != null) {
			if (plc.isConnected()) {
				return true;
			}
		}
		return false;
	}

	public void setLoader(IPlcValuesLoader loader) {
		this.loader = loader;
	}

	public void setParamSender(IPlcParamSender paramSender) {
		this.paramSender = paramSender;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

}
