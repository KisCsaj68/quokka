package com.codecool.quokka.dao.user.implementation;

import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.model.user.Account;

import com.codecool.quokka.model.user.UserDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import java.util.HashSet;
import java.util.Set;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {


    private static Set<Account> DB = new HashSet<>() {{
        UUID id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
        add(new Account("Test User", "test@asd.com", "TestUser", "asd", id));
    }};

    @Override
    public UserDto addUser(String name, String userName, String emailAddress, String passWord) {
        Account newAccount = new Account(name, emailAddress, userName, passWord);
        DB.add(newAccount);
        return new UserDto(userName, newAccount.getId(), name, emailAddress);
    }

    @Override
    public UserDto addUser(Account account) {
        DB.add(account);
        return new UserDto(account.getUserName(), account.getId(), account.getFullName(), account.getEmailAddress());
    }

    @Override
    public Set<UserDto> getAllUser() {
        return this.DB.stream()
                .map(e -> new UserDto(e.getUserName(), e.getId(), e.getFullName(), e.getEmailAddress()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<UserDto> getUser(UUID id) {
        Optional<UserDto> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .map(e -> new UserDto(e.getUserName(), e.getId(), e.getFullName(), e.getEmailAddress()));
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        Optional<Account> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        if (user.isPresent()) {
            DB.remove(user.get());
        }
    }

    @Override
    public Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields) {
        Optional<Account> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        if (user.isPresent()) {
            Account actualAccount = user.get();
            for (String item : fields.keySet()) {
                switch (item) {
                    case "emailAddress":
                        actualAccount.setEmailAddress(fields.get("emailAddress"));
                        break;
                    case "fullName":
                        actualAccount.setFullName(fields.get("fullName"));
                    case "passWord":
                        actualAccount.setPassword(fields.get("passWord"));
                }
            }
            Optional<UserDto> actualDto = Optional.of(new UserDto(actualAccount.getUserName(), actualAccount.getId(),
                    actualAccount.getFullName(), actualAccount.getEmailAddress()));
            return actualDto;
        }
        return Optional.empty();
    }

    @Override
    public boolean getUserByUserName(String userName) {
        return DB.stream().anyMatch(e -> e.getUserName().equals(userName));
    }

    @Override
    public boolean getUserByEmail(String emailAddress) {
        return DB.stream().anyMatch(e -> e.getEmailAddress().equals(emailAddress));
    }
}
