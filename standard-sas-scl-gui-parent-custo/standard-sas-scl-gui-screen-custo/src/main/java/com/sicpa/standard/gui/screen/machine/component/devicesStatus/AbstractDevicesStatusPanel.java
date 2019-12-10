package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

import javax.swing.JPanel;

public abstract class AbstractDevicesStatusPanel extends JPanel {

	protected DevicesStatusModel model;

	public AbstractDevicesStatusPanel(final DevicesStatusModel model) {
		if (model == null) {
			setModel(new DevicesStatusModel());
		} else {
			setModel(model);
		}
	}

	public AbstractDevicesStatusPanel() {
		this(new DevicesStatusModel());
	}

	protected abstract void modelStatusChanged(final DeviceStatusChangeEvent evt);

	protected abstract void modelDeviceAdded(final DeviceStatusChangeEvent evt);

	protected abstract void modelDeviceRemoved(final DeviceStatusChangeEvent evt);

	public void addDevice(final String key, final String label) {
		this.model.addDevice(key, label);
	}

	public void changeStatus(final String key, final DeviceStatus status) {
		this.model.changeStatus(key, status);
	}

	public void setModel(final DevicesStatusModel model) {

		if (this.model == model) {
			return;
		}
		this.model = model;
		this.model.addDeviceStatusChangeListener(new DeviceStatusChangeListener() {

			@Override
			public void statusChanged(final DeviceStatusChangeEvent evt) {
				modelStatusChanged(evt);
			}

			@Override
			public void deviceAdded(final DeviceStatusChangeEvent evt) {
				modelDeviceAdded(evt);
			}

			@Override
			public void deviceRemoved(final DeviceStatusChangeEvent evt) {
				modelDeviceRemoved(evt);
			}
		});
	}

	public DevicesStatusModel getModel() {
		return this.model;
	}
}
