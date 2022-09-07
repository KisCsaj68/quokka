package com.codecool.quokka.model;

public class UserData {

    private String userName;
    private String id;

    public UserData(String userName, String id) {
        this.userName = userName;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getId() {
        return id;
    }
}
