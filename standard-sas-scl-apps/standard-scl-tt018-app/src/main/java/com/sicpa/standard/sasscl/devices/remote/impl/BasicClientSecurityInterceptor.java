package com.sicpa.standard.sasscl.devices.remote.impl;

import org.jboss.ejb.client.EJBClientInterceptor;
import org.jboss.ejb.client.EJBClientInvocationContext;

import com.sicpa.std.server.util.security.ejb.ClientSecurityInterceptor.BasicPrincipal;

public class BasicClientSecurityInterceptor implements EJBClientInterceptor {

	private static BasicPrincipal principal;
	public static final String SECURITY_TOKEN_KEY = com.sicpa.std.server.util.security.ejb.ClientSecurityInterceptor.class
			.getName() + ".SecurityToken";

	public BasicClientSecurityInterceptor() {

	}

	public static void setPrincipal(String username, char[] password) {
		principal = new BasicPrincipal(username, password);
	}

	@Override
	public void handleInvocation(EJBClientInvocationContext context) throws Exception {

        if (principal == null)
            throw new IllegalStateException("no principal set");

        context.getContextData().put(SECURITY_TOKEN_KEY, principal);
		context.sendRequest();
	}

	@Override
	public Object handleInvocationResult(EJBClientInvocationContext context) throws Exception {
		return context.getResult();
	}

}

