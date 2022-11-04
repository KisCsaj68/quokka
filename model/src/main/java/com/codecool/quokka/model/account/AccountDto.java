package com.codecool.quokka.model.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.UUID;
public class AccountDto {

//    @JsonProperty("userName")
    private String userName;
//    @JsonProperty("id")
    private UUID id;

//    @JsonProperty("fullName")
    private String fullName;

//    @JsonProperty("emailAddress")
    private String emailAddress;

    @JsonCreator
    public AccountDto(@JsonProperty("userName") String userName, @JsonProperty("id") UUID id, @JsonProperty("fullName") String fullName, @JsonProperty("emailAddress") String emailAddress) {
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

    public static AccountDto from(Account account) {
        return new AccountDto(account.getUserName(), account.getId(), account.getFullName(), account.getEmailAddress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto accountDto = (AccountDto) o;
        return id.equals(accountDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "userName='" + userName + '\'' +
                ", id=" + id +
                ", fullName='" + fullName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
