package com.codecool.quokka.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.Hashing;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Entity
public class Account {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    private Long id;

    @Id
    @GeneratedValue(generator = "uuid")
    @JsonIgnore
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    //better solution to use Long, and sequence
    private UUID id;
    private String fullName;
    private String userName;
    private String emailAddress;

    private String password;



    public Account(@JsonProperty("full_name") String fullName,
                   @JsonProperty("email_address") String emailAddress,
                   @JsonProperty("user_name") String userName,
                   @JsonProperty("password") String password) {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public Account(String fullName,
                   String emailAddress,
                   String userName,
                   String password,
                   UUID id) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public Account() {

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

    public String getPassword() {
        return password;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(UUID userId) {
        this.id = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    //    public Map<String, String> getUserData() {
//        Map<String, String> data = new HashMap<>();
//        data.put("userName", this.userName);
//        data.put("UserId", this.userId.toString());
//        return data;
//    }

    public void hashPassword() {
        this.password = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) && userName.equals(account.userName) && emailAddress.equals(account.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, emailAddress);
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", fullName='" + fullName + '\'' + ", userName='" + userName + '\'' + ", emailAddress='" + emailAddress + '\'' + ", password='" + password + '\'' + '}';
    }
}

