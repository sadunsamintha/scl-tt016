package com.sicpa.tt016.view.plc;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.tt016.devices.plc.TT016ReloadPLCGroupVarsEvent;

/**
 * Controller to observe PLC var changes at the interface.
 */
public class TT016MultiEditablePlcVariablesController implements TT016PLCEditableVariablesController {

	/**
	 * Send a Reload group vars event to the PLC
	 */
	public void reload() {
		EventBusService.post(new TT016ReloadPLCGroupVarsEvent(this.toString()));
	}
}