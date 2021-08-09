package com.sicpa.tt016.view.plc;

/**
 * An object that controls the requests from a view to interact with a PLC device
 *
 * The interface provides way to reload information from the PLC.
 */
public interface TT016PLCEditableVariablesController {
  void reload();
}
