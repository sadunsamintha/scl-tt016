package com.sicpa.standard.sasscl.devices.plc.remoteserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class TestPlcRemoteServerConnectionHandler {

	private IPlcAdaptor plc;
	private IDevice remoteServer;

	private PlcRemoteServerConnectionHandler handler;

	PlcProvider plcProvider = new PlcProvider();

	IPlcVariable<Integer> registerVar = PlcVariable.createInt32Var(".com.aVar");

	@Before
	public void setup() throws DeviceException {

		plc = mock(IPlcAdaptor.class);
		plcProvider.set(plc);

		remoteServer = mock(IDevice.class);

		handler = new PlcRemoteServerConnectionHandler();
		handler.setPlcProvider(plcProvider);
		handler.setRemoteServer(remoteServer);
		handler.setReqJavaErrorRegisterVar(registerVar);
	}

	@Test
	public void testOnRemoteServerDisconnection() throws PlcAdaptorException {

		Mockito.when(remoteServer.isConnected()).thenReturn(false);
		Mockito.when(plc.isConnected()).thenReturn(true);

		// action
		handler.remoteServerStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, remoteServer));

		registerVar.setValue(1);
		verify(plc).write(registerVar);
	}

	@Test
	public void testOnRemoteServerConnection() throws DeviceException {

		Mockito.when(plc.isConnected()).thenReturn(true);
		Mockito.when(remoteServer.isConnected()).thenReturn(true);

		// action
		remoteServer.connect();
		handler.remoteServerStatusChanged(new DeviceStatusEvent(DeviceStatus.CONNECTED, remoteServer));

		registerVar.setValue(0);
		verify(plc).write(registerVar);
	}

	@Test
	public void testOnPlcConnectionAndRemoteServerConnected() throws DeviceException {

		Mockito.when(plc.isConnected()).thenReturn(true);
		Mockito.when(remoteServer.isConnected()).thenReturn(true);

		// action
		handler.plcStatusChanged(new DeviceStatusEvent(DeviceStatus.CONNECTED, plc));

		registerVar.setValue(0);
		verify(plc).write(registerVar);
	}

	@Test
	public void testOnPlcConnectionAndRemoteServerDisconnected() throws DeviceException {
		Mockito.when(plc.isConnected()).thenReturn(true);
		Mockito.when(remoteServer.isConnected()).thenReturn(false);

		// action
		handler.plcStatusChanged(new DeviceStatusEvent(DeviceStatus.CONNECTED, plc));

		registerVar.setValue(1);
		verify(plc).write(registerVar);
	}
}
