package com.codecool.quokka.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String emailAddress;

    public User(String name, String emailAddress) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
