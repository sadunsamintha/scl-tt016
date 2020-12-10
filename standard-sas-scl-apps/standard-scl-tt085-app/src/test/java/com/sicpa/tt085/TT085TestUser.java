package com.sicpa.tt085;

public enum TT085TestUser {
    OPERATOR("OPERATOR" ), FULL_ADMIN("0");

    private final String login;

    TT085TestUser(final String login) {
        this.login = login;
    }
    public String getLogin() {
        return login;
    }
}