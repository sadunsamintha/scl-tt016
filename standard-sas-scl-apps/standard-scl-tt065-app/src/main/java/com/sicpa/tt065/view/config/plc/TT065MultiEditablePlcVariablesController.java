package com.sicpa.tt065.view.config.plc;

import com.google.common.eventbus.Subscribe;
import java.util.Map;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.event.PlcVarValueChangeEvent;
import com.sicpa.tt065.devices.plc.ReloadPLCGroupVarsEvent;
import com.sicpa.tt065.devices.plc.event.PlcMultiVarValueChangeEvent;

/**
 * Controller to observe PLC var changes, at the interface, and buffer them.
 * Buffered changes will be maintained in memory and will be lost if the application is destroyed.
 * There's no offline nor change backup police.
 */
public class TT065MultiEditablePlcVariablesController implements TT065PLCEditableVariablesController{

  protected TT065PLCMultiVarBuffer buffer = new TT065PLCMultiVarBuffer();

  /**
   * Receives a a PLC value change event and buffer its content
   * @param evt
   */
  @Subscribe
  public void handlePlcVarValueChangeEvent(final PlcVarValueChangeEvent evt) {
    this.buffer.bufferPLCChange(evt.getLogicVarName(), evt.getValue(), evt.getLineIndex());
  }

  /**
   * Send a {@link PlcMultiVarValueChangeEvent} with all current buffered changes.
   * Events will be sent ber line.
   */
  public void pushChangesToPLC(){
    final Map<Integer, Map<String, String>> plcChangesByLine = this.buffer.drainBuffer();
    plcChangesByLine.forEach((line, plcVars) -> EventBusService.post(new PlcMultiVarValueChangeEvent(line, plcVars)));
  }

  /**
   * Clear current buffered changes and request a plc group vars reload
   */
  public void clearChangesAndReload(){
    this.buffer.clearBuffer();
    this.reload();
  }

  /**
   * Send a Reload group vars event to the PLC
   */
  public void reload(){
    EventBusService.post(new ReloadPLCGroupVarsEvent(this.toString()));
  }
}