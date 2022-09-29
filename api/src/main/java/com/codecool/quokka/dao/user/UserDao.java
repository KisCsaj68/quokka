package com.codecool.quokka.dao.user;

import com.codecool.quokka.model.user.Account;
import com.codecool.quokka.model.user.UserDto;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserDao {

     UserDto addUser(String name, String userName, String emailAddress, String passWord);

     UserDto addUser(Account account);
     Set<UserDto> getAllUser();

     Optional<UserDto> getUser(UUID id);

     void deleteUser(UUID id);

     Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields);

    boolean getUserByUserName(String userName);

     boolean getUserByEmail(String emailAddress);
}
