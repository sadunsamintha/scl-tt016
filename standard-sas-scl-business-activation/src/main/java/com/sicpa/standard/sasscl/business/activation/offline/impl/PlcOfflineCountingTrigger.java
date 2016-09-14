package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.activation.offline.IOfflineCounting;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLineIndexes;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCountingTrigger {

	private IOfflineCounting offlineCounting;

	private String quantityVarName;
	private String lastStopTimeVarName;
	private String lastProductTimeVarName;

	public PlcOfflineCountingTrigger(IOfflineCounting offlineCounting) {
		this.offlineCounting = offlineCounting;
	}

	@Subscribe
	public void onProductionStart(final ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)) {

			List<Integer> lineIndexes = getLineIndexes();

			for (Integer lineIndex : lineIndexes) {
				offlineCounting.processOfflineCounting(
						createInt32Var(replaceLinePlaceholder(quantityVarName, lineIndex)),
						createInt32Var(replaceLinePlaceholder(lastStopTimeVarName, lineIndex)),
						createInt32Var(replaceLinePlaceholder(lastProductTimeVarName, lineIndex)));
			}
		}
	}

	public void setQuantityVarName(String quantityVarName) {
		this.quantityVarName = quantityVarName;
	}

	public void setLastStopTimeVarName(String lastStopTimeVarName) {
		this.lastStopTimeVarName = lastStopTimeVarName;
	}

	public void setLastProductTimeVarName(String lastProductTimeVarName) {
		this.lastProductTimeVarName = lastProductTimeVarName;
	}
}