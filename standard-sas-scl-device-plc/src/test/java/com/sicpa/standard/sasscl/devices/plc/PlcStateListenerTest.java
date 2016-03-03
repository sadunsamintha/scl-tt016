package com.sicpa.standard.sasscl.devices.plc;

import static org.mockito.Mockito.mock;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcStateListenerTest {

	IPlcAdaptor plc;
	PlcStateListener stateListener;
	String varName = ".com.aVar";

	@Before
	public void setUp() throws Exception {
		plc = mock(IPlcAdaptor.class);
		stateListener = new PlcStateListener();
		stateListener.setLineStateVarName(varName);
		PlcVariableMap.addLineIndex(1);
	}

	boolean msgCatched = false;

	@Test
	public void notifyStopProductionTest() throws Exception {

		stateListener.processStateChanged(new ApplicationFlowStateChangedEvent(null, ApplicationFlowState.STT_STARTED,
				""));

		Object msgCatcher = new Object() {
			@Subscribe
			public void catchMsg(MessageEvent evt) {
				if (evt.getKey().equals(MessageEventKey.PLC.PLC_STATE_NOT_RUNNING)) {
					msgCatched = true;
				}
			}
		};
		EventBusService.register(msgCatcher);

		stateListener.onPlcEvent(new PlcEvent(varName, 1));

		Assert.assertTrue(msgCatched);

	}
}
