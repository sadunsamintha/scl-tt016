package com.sicpa.tt080;

public enum TT080TestUser {
    OPERATOR("OPERATOR" ), FULL_ADMIN("0");

    private final String login;

    TT080TestUser(final String login) {
        this.login = login;
    }
    public String getLogin() {
        return login;
    }
}