package com.sicpa.standard.sasscl.devices.plc.impl;

import com.sicpa.standard.plc.controller.actions.IPlcAction;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.plc.*;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorConfig;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorNotificationConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sicpa.standard.plc.controller.actions.PlcAction.request;
import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.sasscl.devices.DeviceStatus.STARTED;
import static com.sicpa.standard.sasscl.devices.DeviceStatus.STOPPED;
import static com.sicpa.standard.sasscl.devices.plc.PlcRequest.*;
import static com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorNotificationConfig.createStaticPatternBooleanVar;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PlcTest {

	private PlcAdaptor plc;

	// private PlcSimulatorController simulatorController;

	private Map<PlcRequest, IPlcRequestExecutor> plcRequestActionMap;

	private static final String NTF_STATE = "ntf.state";

	private static final String NTF_ERR_LIFECHECK = "ntf.error.lifeCheck";

	private static final String REQ_START = "req.start";

	private static final String REQ_RUN = "req.run";

	private static final String REQ_STOP = "req.stop";

	private static final String REQ_RELOAD_CONFIG = ".req.reloadConfig";

	private static final String EMERGENCY_STOP_ERR = ".err.emergencyStop";

	private static final String VAR_1 = "Var1";
	private static final String VAR_2 = "Var2";
	private static final String VAR_3 = "Var3";
	private static final PlcEvent EVENT_VAR_1 = new PlcEvent(VAR_1, VAR_1);
	private static final PlcEvent EVENT_VAR_2 = new PlcEvent(VAR_2, VAR_2);
	private static final PlcEvent EVENT_VAR_3 = new PlcEvent(VAR_3, VAR_3);

	@SuppressWarnings({ "serial" })
	@Before
	public void setup() {

		Map<String, IPlcVariable<?>> plcVariableToSet = new HashMap<String, IPlcVariable<?>>();
		Map<String, String> plcVariableMap = new HashMap<String, String>();

		plcVariableMap.put("NTF_WAR_ERR_REGISTER", "COM.stCabinet.stNotifications.nWar_err_register");
		plcVariableMap.put("NTF_LINE_SPEED", ".com.stLine[#x].stNotifications.nLineSpeed");
		plcVariableMap.put("NTF_PRODUCTS_FREQ", ".com.stCabinet.stNotifications.nProductsFreq");

		final IPlcVariable<Integer> warErrVar = PlcVariable
				.createInt32Var("COM.stCabinet.stNotifications.nWar_err_register");

		warErrVar.setValue(0);

		plcVariableToSet.put("COM.stCabinet.stNotifications.nWar_err_register", warErrVar);

		PlcVariableMap.addPlcVariables(plcVariableMap);

		// --------------- setup multiconveyor handler (For )----------------------

		List<IPlcVariable<?>> notificationList = new ArrayList<IPlcVariable<?>>();
		notificationList.add(PlcVariable.createInt32Var(".com.stLine[#x].stNotifications.nWar_err_register"));

		Map<String, String> notificationMap = new HashMap<String, String>();
		notificationMap.put("NTF_LINE_WAR_ERR_REGISTER", ".com.stLine[#x].stNotifications.nWar_err_register");

		plc = new PlcAdaptor(/* simulatorController = */new PlcSimulatorController(new PlcSimulatorConfig() {

			{
				getNotificationVariables().add(new PlcSimulatorNotificationConfig(NTF_STATE, 1, 1));
				getNotificationVariables().add(createStaticPatternBooleanVar(NTF_ERR_LIFECHECK, 1, true));
				getPlcVariables().add(createBooleanVar(REQ_START));
				getPlcVariables().add(createBooleanVar(REQ_RUN));
				getPlcVariables().add(createBooleanVar(REQ_STOP));
				getPlcVariables().add(createBooleanVar(REQ_RELOAD_CONFIG));
				getPlcVariables().add(createBooleanVar(EMERGENCY_STOP_ERR));
				getPlcVariables().add(warErrVar);
			}
		}));

		//
		// add notifications variables into PLC
		plc.getNotificationVariables().add(createInt32Var(NTF_STATE));
		plc.getNotificationVariables().add(createBooleanVar(NTF_ERR_LIFECHECK));

		plcRequestActionMap = new HashMap<PlcRequest, IPlcRequestExecutor>() {
			{
				put(START, createExecutor(request(REQ_START), request(REQ_RUN)));

				put(STOP, createExecutor(request(REQ_STOP)));

				put(RELOAD_PLC_PARAM, createExecutor(request(REQ_RELOAD_CONFIG)));
			}
		};
	}

	private IPlcRequestExecutor createExecutor(IPlcAction... actions) {
		return new DefaultPlcRequestExecutor(actions);
	}

	@Test
	public void allEventsListener() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);
		plc.addPlcListener(listener);

		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);

		verify(listener, times(1)).onPlcEvent(EVENT_VAR_1);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_2);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_3);
	}

	@Test
	public void varsListListener() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);
		plc.addPlcListener(listener, asList(VAR_1, VAR_2));

		plc.firePlcEvent(EVENT_VAR_3);
		verify(listener, never()).onPlcEvent(EVENT_VAR_3);

		plc.firePlcEvent(EVENT_VAR_1);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_1);

		plc.firePlcEvent(EVENT_VAR_2);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_2);
	}

	@Test
	public void allEventsListenerIgnoreToBeAddedAsSpecificVarListener() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);
		// add as all first, then add to listener to var3, but should listener to all
		plc.addPlcListener(listener);
		plc.addPlcListener(listener, asList(VAR_3));

		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);

		verify(listener, times(1)).onPlcEvent(EVENT_VAR_1);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_2);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_3);
	}

	@Test
	public void registeringAsAllVarsListenerOverridesPreviousRegistry() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);
		// add as all after listener to var1, var2, should listen to all as well
		plc.addPlcListener(listener, asList(VAR_1, VAR_2));
		plc.addPlcListener(listener);

		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);

		verify(listener, times(1)).onPlcEvent(EVENT_VAR_1);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_2);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_3);
	}

	@Test
	public void removingOnOneVarAllEventsListenerListensToOtherVars() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);

		plc.addPlcListener(listener, asList(VAR_1, VAR_2, VAR_3));

		plc.addPlcListener(listener);

		// remove only var1, should be able to listen to var2 and var3
		plc.removePlcListener(listener, asList(VAR_1));

		plc.firePlcEvent(EVENT_VAR_1);
		verify(listener, never()).onPlcEvent(EVENT_VAR_1);

		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_2);
		verify(listener, times(1)).onPlcEvent(EVENT_VAR_3);
	}

	@Test
	public void removingListenerForAllEvents() throws Exception {

		IPlcListener listener = mock(IPlcListener.class);
		plc.addPlcListener(listener, asList(VAR_1, VAR_2, VAR_3));
		plc.addPlcListener(listener);
		plc.removePlcListener(listener, asList(VAR_1));

		// remove listener from all variables
		plc.removePlcListener(listener);
		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);
		verify(listener, never()).onPlcEvent(any(PlcEvent.class));
	}

	@Test
	public void unregisteredListener() throws Exception {

		IPlcListener listener2 = mock(IPlcListener.class);

		// send all events
		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);
		verify(listener2, never()).onPlcEvent(any(PlcEvent.class));
	}

	@Test
	public void addRemoveVarsListener() {

		IPlcListener listener2 = mock(IPlcListener.class);
		plc.addPlcListener(listener2, asList(VAR_1, VAR_2, VAR_3));

		plc.removePlcListener(listener2, asList(VAR_2));

		plc.firePlcEvent(EVENT_VAR_1);
		plc.firePlcEvent(EVENT_VAR_2);
		plc.firePlcEvent(EVENT_VAR_3);
		verify(listener2, times(1)).onPlcEvent(EVENT_VAR_1);
		verify(listener2, never()).onPlcEvent(EVENT_VAR_2);
		verify(listener2, times(1)).onPlcEvent(EVENT_VAR_3);
	}

	@Test(expected = PlcAdaptorException.class)
	public void startWithoutConnect() throws Exception {

		plc.start();
	}

	@Test
	public void startPlc() throws DeviceException {

		plc.setPlcRequestActionMap(plcRequestActionMap);

		// setup device status listener
		IDeviceStatusListener deviceStatusListener = mock(IDeviceStatusListener.class);
		plc.addDeviceStatusListener(deviceStatusListener);

		plc.connect();
		plc.start();

		// connected event should be received in device status listener
		ArgumentCaptor<DeviceStatusEvent> deviceStatusArgument = ArgumentCaptor.forClass(DeviceStatusEvent.class);
		verify(deviceStatusListener, atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());
		assertEquals(STARTED, deviceStatusArgument.getValue().getStatus());

		plc.stop();

		verify(deviceStatusListener, atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());
		assertEquals(STOPPED, deviceStatusArgument.getValue().getStatus());

		plc.disconnect();
	}

}
