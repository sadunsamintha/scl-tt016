package com.sicpa.standard.sasscl.devices.remote.impl;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.std.common.api.security.business.LoginServiceHandler;
import com.sicpa.std.common.api.security.dto.LoginDto;
import com.sicpa.std.server.util.locator.ServiceLocator;

/**
 * @see javax.security.auth.spi.LoginModule
 * @author TLeroy
 * 
 */
public class ClientLoginModule implements LoginModule {

	private static final Logger log = LoggerFactory.getLogger(ClientLoginModule.class);
	protected Subject subject;
	protected CallbackHandler callbackHandler;
	protected String userName;
	protected LoginDto loginDto;
	
	public final static String SUBSYSTEM_ID_KEY ="subssytemCode";

	@Override
	public boolean abort() throws LoginException {
		log.debug("** ClientLoginModule * abort called");
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		log.debug("** ClientLoginModule * commit called");
		return true;
	}

	@Override
	public void initialize(final Subject arg0, final CallbackHandler arg1, final Map<String, ?> arg2,
			final Map<String, ?> arg3) {
		log.debug("** ClientLoginModule * initialize called");
		this.subject = arg0;
		this.callbackHandler = arg1;
	}

	@Override
	public boolean login() throws LoginException {
		log.debug("** ClientLoginModule * login called");
		NameCallback name = new NameCallback("name");
		PasswordCallback pwd = new PasswordCallback("pwd", false);
		TextInputCallback objCallback = new TextInputCallback(SUBSYSTEM_ID_KEY);

		try {
			this.callbackHandler.handle(new Callback[] { name, pwd, objCallback });
		} catch (IOException e) {
			throw new LoginException("unexpected problem in login:" + e.getMessage());
		} catch (UnsupportedCallbackException e) {
			throw new LoginException("unexpected problem in login:" + e.getMessage() + " check login configuration ");
		}

		this.userName = name.getName();
		String password = String.valueOf(pwd.getPassword());
//		String subsystemCode = objCallback.getText();
		try {
			LoginServiceHandler securityService = (LoginServiceHandler) ServiceLocator.getInstance().getService(
					ServiceLocator.SERVICE_LOGIN_BUSINESS_SERVICE);
			this.loginDto = securityService.login(this.userName, password);
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}

		return this.loginDto != null;
	}

	@Override
	public boolean logout() throws LoginException {
		log.debug("** ClientLoginModule * logout called");
		this.subject.getPrincipals().clear();
		return true;
	}

}
