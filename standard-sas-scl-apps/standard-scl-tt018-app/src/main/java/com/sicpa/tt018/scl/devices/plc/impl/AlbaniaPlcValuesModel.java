package com.sicpa.tt018.scl.devices.plc.impl;

import java.util.Map;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.tt018.scl.devices.plc.constants.AlbaniaPLCMessages;

public class AlbaniaPlcValuesModel {

	private Map<Integer, PlcVariableDescriptor<?>> mapDistancesValue;

	public PlcVariableDescriptor<?> getValues(int productType) {
		if (!mapDistancesValue.containsKey(productType)) {
			EventBusService.post(new MessageEvent(AlbaniaPLCMessages.EXCEPTION_FAILTOGET_PRODUCTIONVALUE));
		}

		return mapDistancesValue.get(productType);

	}

}
