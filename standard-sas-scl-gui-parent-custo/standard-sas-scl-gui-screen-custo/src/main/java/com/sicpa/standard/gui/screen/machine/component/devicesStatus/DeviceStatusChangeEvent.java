package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

public class DeviceStatusChangeEvent {
	private String key;
	private DeviceStatus oldStatus;
	private DeviceStatus newStatus;
	private Object source;
	private String label;

	public DeviceStatusChangeEvent(final String key, final String label, final DeviceStatus oldStatus,
			final DeviceStatus newStatus) {
		this.key = key;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		this.label = label;
	}

	public String getKey() {
		return this.key;
	}

	public DeviceStatus getOldStatus() {
		return this.oldStatus;
	}

	public DeviceStatus getNewStatus() {
		return this.newStatus;
	}

	public void setSource(final Object source) {
		this.source = source;
	}

	public Object getSource() {
		return this.source;
	}

	public String getLabel() {
		return this.label;
	}
	
	@Override
	public String toString() {
		return this.key+" "+this.label+" "+this.oldStatus+" "+this.newStatus;
	}

}
