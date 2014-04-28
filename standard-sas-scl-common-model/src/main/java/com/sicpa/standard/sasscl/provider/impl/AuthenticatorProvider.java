package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class AuthenticatorProvider extends AbstractProvider<IAuthenticator> {
	
	public AuthenticatorProvider() {
		super("Authenticator");
	}
	
	@Override
	public void set(final IAuthenticator t) {
		super.set(t);

		if (get()!=null&&get().getClass().toString().toUpperCase().contains("SIMULATOR")) {
			EventBusService.post(new MessageEvent(MessageEventKey.Simulator.AUTHENTICATOR));
		}
	}
}
