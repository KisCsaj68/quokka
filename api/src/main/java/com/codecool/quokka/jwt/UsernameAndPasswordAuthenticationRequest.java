package com.codecool.quokka.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsernameAndPasswordAuthenticationRequest {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("password")
    private String password;

    public UsernameAndPasswordAuthenticationRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
