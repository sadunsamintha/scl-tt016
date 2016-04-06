package com.sicpa.standard.sasscl.devices.remote.connector;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.remote.lifecheck.IMasterLifeCheckWorker;

public abstract class AbstractMasterConnector extends AbstractDevice implements IConnectable {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMasterConnector.class);
	private final AtomicBoolean started = new AtomicBoolean();
	private IMasterLifeCheckWorker lifeCheckWorker;

	public AbstractMasterConnector() {
		setName("masterPcc");
	}

	@Override
	public void doConnect() {
		if (started.compareAndSet(false, true)) {
			doLogin();
		}
	}

	@Override
	public void doLogin() {
		try {
			login();
			lifeCheckWorker.start();
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			fireMasterDisconnected();
			logger.error(e.getMessage(), e);
			lifeCheckWorker.scheduleReconnection();
		}
	}

	protected abstract void login() throws Exception;

	@Override
	public void doDisconnect() throws DeviceException {
		if (!started.compareAndSet(true, false)) {
			return;
		}
		lifeCheckWorker.stop();
	}

	public boolean isConnected() {
		return status.isConnected();
	}

	@Override
	public void isAlive() {
		try {
			if (started.get() && checkIsAlive()) {
				fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			} else {
				logger.info("Master not available");
				fireMasterDisconnected();
			}
		} catch (Exception e) {
			logger.error("Remote error", e);
			fireMasterDisconnected();
		}
	}

	protected abstract boolean checkIsAlive();

	protected void fireMasterDisconnected() {
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	protected void fireDeviceStatusChanged(DeviceStatus status) {
		if (this.status != status) {
			logger.info("MPCCConnector status changed: {} ", status.toString());
			this.status = status;
			DeviceStatusEvent evt = new DeviceStatusEvent(status, this);
			synchronized (statusListeners) {
				for (IDeviceStatusListener l : statusListeners) {
					l.deviceStatusChanged(evt);
				}
			}
		}
	}

	public void setLifeCheckWorker(IMasterLifeCheckWorker lifeCheckWorker) {
		this.lifeCheckWorker = lifeCheckWorker;
	}

	public boolean isStarted() {
		return started.get();
	}

}
