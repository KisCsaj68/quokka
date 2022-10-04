package com.codecool.quokka.model.account;

import java.util.Objects;
import java.util.UUID;

public class AccountDto {

    private String userName;
    private UUID id;

    private String fullName;

    private String emailAddress;

    public AccountDto(String userName, UUID id, String fullName, String emailAddress) {
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
}
