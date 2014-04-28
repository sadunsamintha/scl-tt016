/**
 * Author	: YYang
 * Date		: Oct 14, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.actions.IPlcAction;

/**
 * standard PLC command runner - just execute the actions defined
 * 
 * extend this class to perform any other actions after the running the list of actions defined
 * 
 */
public class DefaultPlcRequestExecutor extends AbstractPlcRequestExecutor {

	public DefaultPlcRequestExecutor() {
	}

	public DefaultPlcRequestExecutor(IPlcAction[] plcAcitons) {
		setPlcActions(plcAcitons);
	}

	@Override
	public void execute(IPlcController<?> plcController) throws PlcAdaptorException {
		executeActions(plcController);
	}

}
