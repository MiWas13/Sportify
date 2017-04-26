package com.michael.sportify.Authorization;

/**
 * Created by Michael on 27.04.16.
 */
public class AuthorizationItem {
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private String login;

}
