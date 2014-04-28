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
 * 
 * Execute PLC requests
 * 
 */
public interface IPlcRequestExecutor {

	/**
	 * to run the command
	 * 
	 * @throws PlcAdaptorException
	 */
	void execute(IPlcController<?> plcController) throws PlcAdaptorException;

	/**
	 * 
	 * set plc actions to the command
	 * 
	 * @param plcActions
	 */
	void setPlcActions(IPlcAction[] plcActions);

	/**
	 * add plc action to existing action list
	 * 
	 * @param plcAction
	 */
	void addPlcAction(IPlcAction plcAction);
}
