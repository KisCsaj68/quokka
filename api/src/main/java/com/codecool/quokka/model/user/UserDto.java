package com.codecool.quokka.model.user;

import java.util.Objects;
import java.util.UUID;

public class UserDto {

    private String userName;
    private UUID id;

    private String fullName;

    private String emailAddress;

    public UserDto(String userName, UUID id, String fullName, String emailAddress) {
        this.userName = userName;
        this.id = id;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
    }




    public String getUserName() {
        return userName;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return id.equals(userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
