package com.codecool.quokka.model;

import java.util.UUID;

public class UserDto {

    private String userName;
    private UUID id;

    public UserDto(String userName, UUID id) {
        this.userName = userName;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public UUID getId() {
        return id;
    }
}
