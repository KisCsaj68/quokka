package com.codecool.quokka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class User {
    private UUID id;
    private String fullName;
    private String userName;
    private String emailAddress;
    private String password;

    public User(@JsonProperty("fullName") String fullName,
                @JsonProperty("emailAddress") String emailAddress,
                @JsonProperty("userName") String userName,
                @JsonProperty("passWord") String password)
    {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, String> getUserData() {
        Map<String, String> data = new HashMap<>();
        data.put("userName", this.userName);
        data.put("UserId",this.id.toString());
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
