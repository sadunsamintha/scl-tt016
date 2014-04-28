/**
 * Author	: YYang
 * Date		: Oct 6, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.impl;

import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.notification.IPlcNotificationHandler;
import com.sicpa.standard.plc.controller.notification.PlcNotificationHandler;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcBoolean;
import com.sicpa.standard.plc.value.PlcInt32;

public class PlcNotificationHandlerForTesting implements IPlcNotificationHandler {

	@PlcNotificationHandler(variableName = PlcSimulatorControllerTest.LIFE_CHECK_VAR, variableType = PlcBoolean.class)
	public void lifeCheckNotification(final IPlcController<?> sender, final IPlcVariable<Boolean> variable,
			final Boolean val) {
	}

	@PlcNotificationHandler(variableName = PlcSimulatorControllerTest.INT_VAR_1, variableType = PlcInt32.class)
	public void intVarNotification(final IPlcController<?> sender, final IPlcVariable<Integer> variable,
			final Integer val) {
	}
}
