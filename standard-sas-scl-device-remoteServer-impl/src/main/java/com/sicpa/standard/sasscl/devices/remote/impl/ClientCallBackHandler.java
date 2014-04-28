package com.sicpa.standard.sasscl.devices.remote.impl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class ClientCallBackHandler implements CallbackHandler {

	protected String username;
	protected char[] password;
	protected String subsystemId;

	public ClientCallBackHandler(String username, String password, String subsystemId) {
		this.username = username;
		this.password = password.toCharArray();
		this.subsystemId = subsystemId;
	}

	@Override
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				NameCallback nc = (NameCallback) callback;
				nc.setName(this.username);
			} else if (callback instanceof PasswordCallback) {
				PasswordCallback pc = (PasswordCallback) callback;
				pc.setPassword(this.password);
			}
			// this is not needed the subsystem is link to the user used to connect to the remote server
			// moreover the subsystem id is now retreived from the remote server itself
			// else if (callback instanceof TextInputCallback) {
			// if (((TextInputCallback) callback).getPrompt().equals(ClientLoginModule.SUBSYSTEM_ID_KEY)) {
			// ((TextInputCallback) callback).setText(this.subsystemId);
			// }
			// }
		}
	}
}