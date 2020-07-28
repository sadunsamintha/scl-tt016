package com.sicpa.tt084.client.common.security;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityModel;
import com.sicpa.standard.client.common.security.User;

import java.util.List;

public class TT084SecurityModelWrapper<T extends SecurityModel> {
    private static final Permission ALL_PERMISSIONS = new Permission(".*");

    private T securityModel;

    public TT084SecurityModelWrapper(final T securityModel) {
        this.securityModel = securityModel;
    }

    public User getDefaultUser(){
        return this.securityModel.getDefaultUser();
    }

    public List<User> getUsers(){
        return this.securityModel.getUsers();
    }

    public User getUserByLogin(final String login) {
        return securityModel.getUsers().stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst().get();
    }

    public boolean hasAllPermissions(final User user){
        return user.hasPermission(ALL_PERMISSIONS);
    }
}