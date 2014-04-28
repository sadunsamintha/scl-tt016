package com.sicpa.standard.sasscl.event;

public class LoginAttemptEvent {
	
	private String login;
	
	private String password;
	
	public LoginAttemptEvent(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}
