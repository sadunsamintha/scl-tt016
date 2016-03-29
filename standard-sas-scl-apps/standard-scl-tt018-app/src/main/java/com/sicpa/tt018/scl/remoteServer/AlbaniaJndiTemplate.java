package com.sicpa.tt018.scl.remoteServer;

import static com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerConstants.PROPERTY_PASSWORD;
import static com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerConstants.PROPERTY_USERNAME;
import static com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerConstants.SUBSYSTEM_ID_PROPERTY;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.jndi.JndiTemplate;

import com.sicpa.tt018.common.security.CustomPrincipal;

public class AlbaniaJndiTemplate extends JndiTemplate {
	@Override
	protected Context createInitialContext() throws NamingException {

		Hashtable<String, Object> icEnv = null;
		Properties env = getEnvironment();
		if (env != null) {
			icEnv = new Hashtable<String, Object>(env.size() + 2);
			for (Enumeration<?> en = env.propertyNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				icEnv.put(key, env.getProperty(key));
			}
			icEnv.put(
					SECURITY_PRINCIPAL,
					new CustomPrincipal(env.getProperty(PROPERTY_USERNAME), Integer.valueOf(env.get(
							SUBSYSTEM_ID_PROPERTY).toString())));
			icEnv.put(SECURITY_CREDENTIALS, env.getProperty(PROPERTY_PASSWORD));
		}
		return new InitialContext(icEnv);
	}

}
