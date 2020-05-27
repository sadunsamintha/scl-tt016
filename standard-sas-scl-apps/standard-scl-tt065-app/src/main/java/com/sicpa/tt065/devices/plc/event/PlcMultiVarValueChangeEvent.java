package com.sicpa.tt065.devices.plc.event;

import java.util.Collections;
import java.util.Map;

/**
 * Event to notify that multiple PLC vars have it's values updated
 */
public class PlcMultiVarValueChangeEvent {

  private final int lineId;
  private final Map<String, String> plcVars;

  /**
   *
   * @param lineId Line identificator
   * @param plcVars Map with updated plc vars. Keys should be the PLC variable name, and values, the updated values
   */
  public PlcMultiVarValueChangeEvent(final int lineId, final Map<String, String> plcVars) {
    this.lineId = lineId;
    this.plcVars= Collections.unmodifiableMap(plcVars);
  }

  public int getLineId() {
    return lineId;
  }

  /**
   * Returns a unmodifiable version of the map with updated values.
   * Keys are the plc logical name
   * @return
   */
  public Map<String, String> getPlcVars() {
    return this.plcVars;
  }
}