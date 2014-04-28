package com.sicpa.standard.sasscl.event;

import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.sasscl.security.UserId;

public class PlcLoginEvent {
	
	private UserId userId;
	private User appUserProfile;
	
	public PlcLoginEvent(UserId userId, User appUserProfile) {
		super();
		this.userId = userId;
		this.appUserProfile = appUserProfile;
	}
	
	public User getAppUserProfile() {
		return appUserProfile;
	}
	
	public UserId getUserId() {
		return userId;
	}
}
