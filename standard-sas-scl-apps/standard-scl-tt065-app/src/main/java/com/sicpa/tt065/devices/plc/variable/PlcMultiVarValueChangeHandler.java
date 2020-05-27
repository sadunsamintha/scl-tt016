package com.sicpa.tt065.devices.plc.variable;

import com.google.common.eventbus.Subscribe;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.tt065.devices.plc.event.PlcMultiVarValueChangeEvent;

/**
 * Handles PlC multi vars value updates, parsing the values and sending update requests to PLC device.
 */
public final class PlcMultiVarValueChangeHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlcMultiVarValueChangeHandler.class);

  private PlcProvider plcProvider;
  private IPlcParamSender paramSender;
  private IPlcValuesLoader loader;

  /**
   * Request to PLC to updated all vars presented on the received event.
   *
   * This will only occurs if the PLC is connect, otherwise this event will be ignored.
   * After all updates have been requested to PLC a request to reload all PLC vars will be sent as well.
   *
   * Errors during the PLC update process will be notified via an error {@code MessageEvent}.
   *
   * @param evt
   */
  @Subscribe
  public void handlePlcMultiVarValueChangeEvent(final PlcMultiVarValueChangeEvent evt) {
    final int lineId = evt.getLineId();
    final Map<String, String> plcVars = evt.getPlcVars();
    try{
      if (!shouldBeSentToPlc()) {
        LOGGER.debug("PLC is not presented or disconnect. All received updates will be ignored");
        return;
      }

      plcVars.forEach((varName, value) -> saveAndSendValueToPlc(varName, value, lineId));
      plcProvider.get().executeRequest(PlcRequest.RELOAD_PLC_PARAM);

    }catch (PlcAdaptorException e){
      LOGGER.error("Unable to Reload PLC Parameters", e);
      EventBusService.post(new MessageEvent(this, "Error while reload PLC params", e));
    }
  }

  private void saveAndSendValueToPlc(String varName, String value, int lineIndex) {
    saveValue(varName, value, lineIndex);
    try {
      EventBusService.post(new WarningViewEvent("plc.config.var.warning", false, varName));
      paramSender.sendToPlc(varName, value, lineIndex);
    } catch (PlcAdaptorException e) {
      LOGGER.error("", e);
      EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, e, varName));
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