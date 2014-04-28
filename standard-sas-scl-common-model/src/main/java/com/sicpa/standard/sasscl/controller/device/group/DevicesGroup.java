package com.sicpa.standard.sasscl.controller.device.group;

/**
 * This class provides a set of defined groups to identify a group of devices. It also allows to extend it or use a
 * <code>String</code> name to create new groups. Note that group names are case sensitive.
 * 
 * @author JPerez
 * 
 */
public class DevicesGroup {

	protected String groupName;

	public final static DevicesGroup SELECTION_SCREEN_GROUP = new DevicesGroup("selectionScreen");
	public final static DevicesGroup STARTUP_GROUP = new DevicesGroup("startup");

	public DevicesGroup(final String groupName) {
		this.groupName = groupName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.groupName == null) ? 0 : this.groupName.hashCode());
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
		DevicesGroup other = (DevicesGroup) obj;
		if (this.groupName == null) {
			if (other.groupName != null) {
				return false;
			}
		} else if (!this.groupName.equals(other.groupName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.groupName;
	}

}
