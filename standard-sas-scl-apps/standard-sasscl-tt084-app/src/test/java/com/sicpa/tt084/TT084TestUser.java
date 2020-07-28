package com.sicpa.tt084;

public enum TT084TestUser {
    OPERATOR("OPERATOR" ), FULL_ADMIN("0");

    private final String login;

    TT084TestUser(final String login) {
        this.login = login;
    }
    public String getLogin() {
        return login;
    }
}