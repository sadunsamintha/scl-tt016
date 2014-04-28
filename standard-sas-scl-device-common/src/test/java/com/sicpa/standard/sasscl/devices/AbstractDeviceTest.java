package com.sicpa.standard.sasscl.devices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AbstractDeviceTest {

	private AbstractDeviceImpl deviceImpl;

	private IDeviceStatusListener deviceStatusListener;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(AbstractDeviceTest.class);
		deviceImpl = new AbstractDeviceImpl();
		deviceStatusListener = Mockito.mock(IDeviceStatusListener.class);
	}

	@Test
	public void testAddDeviceStatusListener() {
		checkAddDeviceStatusListener();
	}

	@Test
	public void testRemoveDeviceStatusListener() {
		checkAddDeviceStatusListener();

		deviceImpl.removeDeviceStatusListener(deviceStatusListener);

		assertEquals(0, deviceImpl.statusListeners.size());
	}

	@Test
	public void testFireDeviceStatusChanged() {
		checkAddDeviceStatusListener();

		deviceImpl.fireDeviceStatusChanged(DeviceStatus.CONNECTED);

		assertEquals(DeviceStatus.CONNECTED, deviceImpl.getStatus());

		verifyDeviceStatusChangedInvoked(DeviceStatus.CONNECTED);
	}

	@Test
	public void testConnect() throws Exception {
		deviceImpl.setStatus(DeviceStatus.STARTED);

		checkAddDeviceStatusListener();

		deviceImpl.connect();

		assertTrue(deviceImpl.doConnecteCalled);

		verifyDeviceStatusChangedInvoked(DeviceStatus.CONNECTING);
	}

	@Test
	public void testConnectAlreadyConnecting() throws Exception {
		deviceImpl.setStatus(DeviceStatus.CONNECTING);

		deviceImpl.connect();

		assertFalse(deviceImpl.doConnecteCalled);

	}

	@Test
	public void testConnectAlreadyConnected() throws Exception {
		deviceImpl.setStatus(DeviceStatus.CONNECTED);

		deviceImpl.connect();

		assertFalse(deviceImpl.doConnecteCalled);

	}

	@Test
	public void testDisconnect() {
		deviceImpl.setStatus(DeviceStatus.CONNECTED);

		checkAddDeviceStatusListener();

		deviceImpl.disconnect();

		assertTrue(deviceImpl.doDisconnectCalled);

		verifyDeviceStatusChangedInvoked(DeviceStatus.DISCONNECTING);
	}

	@Test
	public void testDisconnectException() {
		deviceImpl.setStatus(DeviceStatus.CONNECTED);

		deviceImpl.throwDeviceException = true;

		checkAddDeviceStatusListener();

		deviceImpl.disconnect();

		assertTrue(deviceImpl.doDisconnectCalled);

		verifyDeviceStatusChangedInvoked(DeviceStatus.DISCONNECTING);
	}

	@Test
	public void testIsConnected() {
		deviceImpl.setStatus(DeviceStatus.CONNECTED);

		assertTrue(deviceImpl.isConnected());

		deviceImpl.setStatus(DeviceStatus.DISCONNECTED);

		assertFalse(deviceImpl.isConnected());
	}

	public static class AbstractDeviceImpl extends AbstractDevice {

		public static final String NAME = "NAME";
		public boolean doConnecteCalled;
		public boolean doDisconnectCalled;
		public boolean throwDeviceException;

		public AbstractDeviceImpl() {
			super();
			setName("NAME");
		}

		@Override
		protected void doConnect() throws DeviceException {
			doConnecteCalled = true;

		}

		@Override
		protected void doDisconnect() throws DeviceException {
			doDisconnectCalled = true;
			if (throwDeviceException) {
				throw new DeviceException() {
					private static final long serialVersionUID = 8776646740015012503L;
				};
			}

		}

		public void setStatus(DeviceStatus deviceStatus) {
			super.status = deviceStatus;
		}
	}

	private void checkAddDeviceStatusListener() {
		deviceImpl.addDeviceStatusListener(deviceStatusListener);
		assertEquals(1, deviceImpl.statusListeners.size());
		assertEquals(deviceStatusListener, deviceImpl.statusListeners.get(0));
	}

	private class EqDeviceStatusEvent extends ArgumentMatcher<DeviceStatusEvent> {

		private DeviceStatusEvent deviceStatusEvent;

		public EqDeviceStatusEvent(DeviceStatusEvent deviceStatusEvent) {
			this.deviceStatusEvent = deviceStatusEvent;
		}

		@Override
		public boolean matches(Object argument) {
			if (this == argument)
				return true;
			if (argument == null)
				return false;
			DeviceStatusEvent other = (DeviceStatusEvent) argument;
			if (deviceStatusEvent.getDevice() == null) {
				if (other.getDevice() != null)
					return false;
			} else if (!deviceStatusEvent.getDevice().equals(other.getDevice()))
				return false;
			if (deviceStatusEvent.getStatus() == null) {
				if (other.getStatus() != null)
					return false;
			} else if (!deviceStatusEvent.getStatus().equals(other.getStatus()))
				return false;
			return true;
		}

	}

	private void verifyDeviceStatusChangedInvoked(DeviceStatus deviceStatus) {
		Mockito.verify(deviceStatusListener).deviceStatusChanged(
				(DeviceStatusEvent) Mockito.argThat(new EqDeviceStatusEvent(new DeviceStatusEvent(deviceStatus,
						deviceImpl))));
	}
}
