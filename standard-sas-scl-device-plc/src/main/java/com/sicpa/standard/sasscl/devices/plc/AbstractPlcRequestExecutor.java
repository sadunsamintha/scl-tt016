/**
 * Author	: YYang
 * Date		: Oct 14, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.actions.IPlcAction;

public abstract class AbstractPlcRequestExecutor implements IPlcRequestExecutor {

	protected final List<IPlcAction> plcActions = new ArrayList<IPlcAction>();

	public void executeActions(IPlcController<?> plcController) throws PlcAdaptorException {
		try {
			if (plcActions != null && plcActions.size() > 0) {
				plcController.createRequest(this.plcActions.toArray(new IPlcAction[0])).execute();
			}
		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
	}

	protected IPlcAction[] getPlcActions() {
		return plcActions.toArray(new IPlcAction[0]);
	}

	public void setPlcActions(IPlcAction[] plcActions) {
		this.plcActions.clear();
		for (IPlcAction plcAction : plcActions) {
			addPlcAction(plcAction);
		}
	}

	@Override
	public void addPlcAction(IPlcAction plcAction) {
		this.plcActions.add(plcAction);
	}
}
