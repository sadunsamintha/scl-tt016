package com.sicpa.standard.sasscl.devices.plc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcStateListener extends PlcListenerAdaptor {

	private static Logger logger = LoggerFactory.getLogger(PlcStateListener.class);

	private String lineStateVarName;

	protected int stoppedStateValue = 1;
	protected ApplicationFlowState currentAppState = ApplicationFlowState.STT_NO_SELECTION;

	public PlcStateListener() {

	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				plcProvider.get().addPlcListener(PlcStateListener.this);
			}
		});
	}

	@Override
	public void onPlcEvent(PlcEvent event) {

		List<String> lineStateVariablesName = PlcLineHelper.getLinesVariableName(lineStateVarName);

		if (lineStateVariablesName != null && lineStateVariablesName.contains(event.getVarName())) {

			if (stoppedStateValue < 0) {
				logger.error("plc runningStateValue not set");
				return;
			}

			// STDSASSCL-971
			ThreadUtils.sleepQuietly(50);

			if (currentAppState.equals(ApplicationFlowState.STT_STARTED)) {
				if (event.getValue() instanceof Integer) {
					Integer currentState = (Integer) event.getValue();
					if (currentState.intValue() == stoppedStateValue) {
						onPlcStopped(currentState);
					}
				}
			}
		}
	}

	protected void onPlcStopped(int currentState) {
		EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.PLC_STATE_NOT_RUNNING, currentState));
	}

	public void setStoppedStateValue(int stoppedStateValue) {
		this.stoppedStateValue = stoppedStateValue;
	}

	public int getStoppedStateValue() {
		return stoppedStateValue;
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentAppState = evt.getCurrentState();
	}

	public void setLineStateVarName(String lineStateVarName) {
		this.lineStateVarName = lineStateVarName;
	}
}
