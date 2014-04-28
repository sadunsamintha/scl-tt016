package com.sicpa.standard.sasscl.devices.remote;

import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;

/**
 * 
 * Abstract class to be extended by new Remote Server implementation
 * 
 */
public abstract class AbstractRemoteServer extends AbstractDevice implements IRemoteServer {

	protected ICryptoFieldsConfig cryptoFieldsConfig;

	public AbstractRemoteServer() {
		super();
		setName("remoteServer");
	}

	@Override
	protected abstract void doConnect() throws RemoteServerException;

	@Override
	protected abstract void doDisconnect() throws RemoteServerException;

	public ICryptoFieldsConfig getCryptoFieldsConfig() {
		return cryptoFieldsConfig;
	}

	public void setCryptoFieldsConfig(final ICryptoFieldsConfig cryptoFieldsConfig) {
		this.cryptoFieldsConfig = cryptoFieldsConfig;
	}
}
