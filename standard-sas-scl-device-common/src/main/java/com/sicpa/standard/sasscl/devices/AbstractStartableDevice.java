package com.sicpa.standard.sasscl.devices;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStartableDevice extends AbstractDevice implements IStartableDevice {
	protected final List<Runnable> stopActions = new ArrayList<Runnable>();
	protected final List<Runnable> startActions = new ArrayList<Runnable>();

	@Override
	public void stop() throws DeviceException {
		synchronized (lock) {
			try {
				for (Runnable task : stopActions) {
					task.run();
				}
				doStop();
			} catch (Exception e) {
				throw new DeviceException(e);
			}
		}
	}

	protected abstract void doStop() throws DeviceException;

	@Override
	public void start() throws DeviceException {
		synchronized (lock) {
			try {
				for (Runnable task : startActions) {
					task.run();
				}
				doStart();
			} catch (Exception e) {
				throw new DeviceException(e);
			}
		}
	}

	protected abstract void doStart() throws DeviceException;

	public AbstractStartableDevice() {
	}

	public void addStopAction(Runnable stopAction) {
		stopActions.add(stopAction);
	}

	public void addStartAction(Runnable startAction) {
		startActions.add(startAction);
	}

}
