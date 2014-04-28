package com.sicpa.standard.sasscl.controller.hardware;

public class HardwareControllerStatus {

	private String name;

	public final static HardwareControllerStatus DISCONNECTED = new HardwareControllerStatus("DISCONNECTED");
	public final static HardwareControllerStatus CONNECTING = new HardwareControllerStatus("CONNECTING");
	public final static HardwareControllerStatus CONNECTED = new HardwareControllerStatus("CONNECTED");
	public final static HardwareControllerStatus STARTED = new HardwareControllerStatus("STARTED");
	public final static HardwareControllerStatus STARTING = new HardwareControllerStatus("STARTING");
	public final static HardwareControllerStatus STOPPING = new HardwareControllerStatus("STOPPING");
	public final static HardwareControllerStatus DISCONNECTING = new HardwareControllerStatus("DISCONNECTING");

	public HardwareControllerStatus() {
	}

	public HardwareControllerStatus(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "HardwareStatus [name=" + this.name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HardwareControllerStatus other = (HardwareControllerStatus) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
