package com.sicpa.standard.sasscl.devices.brs;

import static com.sicpa.standard.client.common.eventbus.service.EventBusService.post;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.LINE_DISABLE;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.RESET_EXPECTED_SKU;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.RESET_SKU_CHECK_MODE;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.SET_EXPECTED_SKU;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.SET_SKU_CHECK_MODE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBusService.class)
public class BrsStateListenerTest {

	static final String MESSAGE = "for whatever reason";
	private PlcAdaptor plcAdaptor;
	private BrsStateListener brsStateListener;
	private Map<BrsPlcRequest, IPlcVariable<?>> brsRequestMap = new HashMap<BrsPlcRequest, IPlcVariable<?>>(); 
	
	@Before
	public void setUp() throws Exception {

		brsRequestMap.put(BrsPlcRequest.LINE_DISABLE, Mockito.mock(PlcVariable.class));
		brsRequestMap.put(BrsPlcRequest.LINE_ENABLE, Mockito.mock(PlcVariable.class));
		brsRequestMap.put(BrsPlcRequest.SET_EXPECTED_SKU, Mockito.mock(PlcVariable.class));
		brsRequestMap.put(BrsPlcRequest.SET_SKU_CHECK_MODE, Mockito.mock(PlcVariable.class));		
				
		plcAdaptor = Mockito.mock(PlcAdaptor.class);
		brsStateListener = new BrsStateListener();
		brsStateListener.setPlcAdaptor(plcAdaptor);	
		brsStateListener.setBrsRequestMap(brsRequestMap);		
	}

	@Ignore
	@Test
	public void onStateReady() throws Exception {

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_CONNECTING, ApplicationFlowState.STT_CONNECTED, ""));
		
		verify(plcAdaptor, times(1)).executeRequest(RESET_EXPECTED_SKU);
		verify(plcAdaptor, times(1)).executeRequest(RESET_SKU_CHECK_MODE);
		assertThat(brsStateListener.isStopped(), equalTo(true));
	}

	@Ignore
	@Test
	public void onStateExiting() throws Exception {

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_NO_SELECTION, ApplicationFlowState.STT_EXIT, ""));
		
		verify(plcAdaptor, times(1)).executeRequest(RESET_EXPECTED_SKU);
		assertThat(brsStateListener.isStopped(), equalTo(true));
	}

	@Test
	public void onStateStarted() throws Exception {

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING, ApplicationFlowState.STT_STARTED, ""));
						
		verify(plcAdaptor, times(1)).write(brsRequestMap.get(SET_EXPECTED_SKU));
		verify(plcAdaptor, times(1)).write(brsRequestMap.get(SET_SKU_CHECK_MODE));	

		assertThat(brsStateListener.isStopped(), equalTo(false));
	}

	@Test
	public void onStateStopping() throws Exception {

		when(plcAdaptor.isConnected()).thenReturn(true);

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTED, ApplicationFlowState.STT_STOPPING, ""));
		
		verify(plcAdaptor, times(1)).write(brsRequestMap.get(RESET_EXPECTED_SKU));
		verify(plcAdaptor, times(1)).write(brsRequestMap.get(LINE_DISABLE));
	
		assertThat(brsStateListener.isStopped(), equalTo(true));
	}

	@Test
	public void onFailedStateTransition() throws Exception {

		PowerMockito.mockStatic(EventBusService.class);

		doThrow(new PlcAdaptorException(MESSAGE)).when(plcAdaptor).executeRequest(Mockito.<PlcRequest> any());

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTED, ApplicationFlowState.STT_STOPPING, ""));
		
		post(new MessageEvent(plcAdaptor, MessageEventKey.BRS.BRS_PLC_CAMERAS_DISCONNECTION, MESSAGE));		
	}

	@Test
	public void onUnhandledState() throws Exception {

		brsStateListener.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_SELECT_NO_PREVIOUS, ApplicationFlowState.STT_NO_SELECTION, ""));
		
		verifyZeroInteractions(plcAdaptor);
	}
}
