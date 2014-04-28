package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.model.IRemoteServerModel;

/**
 * Standard remote server model implementation
 */
public class RemoteServerModel implements IRemoteServerModel {

	protected String username;

	protected String password;

	/**
	 * sleep time between 2 lifechecking, in SECONDS
	 */
	protected int lifeCheckSleep;

	public RemoteServerModel() {
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public int getLifeCheckSleep() {
		return this.lifeCheckSleep;
	}

	public void setLifeCheckSleep(final int lifeCheckSleep) {
		this.lifeCheckSleep = lifeCheckSleep;
	}
}
