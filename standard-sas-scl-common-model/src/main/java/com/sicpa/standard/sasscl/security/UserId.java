package com.sicpa.standard.sasscl.security;

public class UserId {
		
	private String firstname;
	private String surname;	
	private int userID;
	private String userLevelAccess;
	/** there is possible to have the same login for several userIds */
	private String login;
	
	/**
	 * for Testing purposes
	 * @param login
	 * @param firstname
	 * @param surname
	 * @param userID
	 * @param userLevelAccess
	 */
	public UserId(String login, String firstName, String surname, int userId, String userLevelAccess) {
		super();
		this.login = login;
		this.firstname = firstName;
		this.surname = surname;
		this.userID = userId;
		this.userLevelAccess = userLevelAccess;
	}
	
	public UserId() {
		
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstName) {
		this.firstname = firstName;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userId) {
		this.userID = userId;
	}
	public String getUserLevelAccess() {
		return userLevelAccess;
	}
	public void setUserLevelAccess(String userLevelAccess) {
		this.userLevelAccess = userLevelAccess;
	}

}
