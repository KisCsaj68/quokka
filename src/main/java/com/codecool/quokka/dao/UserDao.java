package com.codecool.quokka.dao;

import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserDao {

     UserDto addUser(String name, String userName, String emailAddress, String passWord);

     UserDto addUser(User user);
     Set<UserDto> getAllUser();

     Optional<UserDto> getUser(UUID id);

     void deleteUser(UUID id);

     Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields);

}
