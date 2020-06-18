package com.sicpa.tt065.view.plc;

/**
 * An object that controls the requests from a view to interact with a PLC device
 *
 * The interface provides ways to push changes to a PLC, clear any data it held and
 * reload information from the PLC.
 */
public interface TT065PLCEditableVariablesController {
  void pushChangesToPLC();
  void clearChangesAndReload();
  void reload();
}
