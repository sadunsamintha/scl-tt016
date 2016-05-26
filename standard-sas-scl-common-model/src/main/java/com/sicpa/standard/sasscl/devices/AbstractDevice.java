package com.sicpa.standard.sasscl.devices;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.messages.IssueSolvedMessage;

/**
 * abstract class for all devices
 *
 * @author DIelsch
 *
 */
public abstract class AbstractDevice implements IDevice {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDevice.class);

	protected final ArrayList<IDeviceStatusListener> statusListeners = new ArrayList<IDeviceStatusListener>();

	protected DeviceStatus status;

	protected final Object lock = new Object();

	protected String name;

	public AbstractDevice() {
		status = DeviceStatus.DISCONNECTED;
	}

	@Override
    public void addDeviceStatusListener(final IDeviceStatusListener listener) {
		synchronized (statusListeners) {
			statusListeners.add(listener);
		}
	}

	@Override
    public void removeDeviceStatusListener(final IDeviceStatusListener listener) {
		synchronized (statusListeners) {
			statusListeners.remove(listener);
		}
	}

	public void clearAllDeviceStatusListeners() {
		synchronized (statusListeners) {
			statusListeners.clear();
		}
	}

	protected void fireDeviceStatusChanged(final DeviceStatus status) {

		if (this.status != status) {
			logger.info("Device Status Changed: device - {} , status - {} ", getName(), status.toString());

			this.status = status;
			DeviceStatusEvent evt = new DeviceStatusEvent(status, this);

			synchronized (statusListeners) {
				for (IDeviceStatusListener l : statusListeners) {
					l.deviceStatusChanged(evt);
				}
			}
		}
	}

	@Override
	public void connect() throws DeviceException {
		synchronized (lock) {
			if (status != DeviceStatus.CONNECTING && status != DeviceStatus.CONNECTED) {
				fireDeviceStatusChanged(DeviceStatus.CONNECTING);
				doConnect();
			}
		}
	}

	@Override
	public void disconnect() {
		synchronized (lock) {
			if (status != DeviceStatus.DISCONNECTING && status != DeviceStatus.DISCONNECTED) {
				fireDeviceStatusChanged(DeviceStatus.DISCONNECTING);
			}
			try {
				doDisconnect();
			} catch (DeviceException e) {
				logger.error(MessageFormat.format("Error when disconnecting {0}", getClass().getName()), e);
			}
		}
	}

	protected abstract void doConnect() throws DeviceException;

	protected abstract void doDisconnect() throws DeviceException;

	@Override
	public boolean isConnected() {
		return this.status.isConnected();
	}

	protected void fireMessage(final String key, final Object... params) {
		logger.debug("Device Message Changed: device - {}, message - {}", this, Messages.get(key));
		MessageEvent evt = new MessageEvent(this, key, params);
		EventBusService.post(evt);
	}

	@Override
	public DeviceStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	protected void fireIssueSolved(String key) {
		IssueSolvedMessage evt = new IssueSolvedMessage(key, this);
		EventBusService.post(evt);
	}

}
