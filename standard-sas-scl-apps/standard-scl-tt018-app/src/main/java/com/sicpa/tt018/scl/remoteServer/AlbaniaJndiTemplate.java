package com.sicpa.tt018.scl.remoteServer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.jndi.JndiTemplate;

import com.sicpa.tt018.common.security.CustomPrincipal;
import com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerConstants;

public class AlbaniaJndiTemplate extends JndiTemplate {
	@Override
	protected Context createInitialContext() throws NamingException {
		Hashtable<String, Object> icEnv = null;
		final Properties env = getEnvironment();
		if (env != null) {
			icEnv = new Hashtable<String, Object>(env.size() + 2);
			for (final Enumeration<?> en = env.propertyNames(); en.hasMoreElements();) {
				final String key = (String) en.nextElement();
				icEnv.put(key, env.getProperty(key));
			}
			icEnv.put(Context.SECURITY_PRINCIPAL, new CustomPrincipal(env.getProperty(AlbaniaRemoteServerConstants.PROPERTY_USERNAME), Integer.valueOf(env.get(AlbaniaRemoteServerConstants.SUBSYSTEM_ID_PROPERTY).toString())));
			icEnv.put(Context.SECURITY_CREDENTIALS, env.getProperty(AlbaniaRemoteServerConstants.PROPERTY_PASSWORD));
		}
		return new InitialContext(icEnv);
	}

}
