package com.codecool.quokka.service.user;

import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.model.user.User;

import com.codecool.quokka.model.user.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("inMemoUserDao") UserDao userDao) {
        this.userDao = userDao;
    }



    public UserDto addUser(User user) {
        return userDao.addUser(user);
    }

    public Set<UserDto> getAllUser() {
        return userDao.getAllUser();
    }

    public Optional<UserDto> getUser(UUID id) {
        return userDao.getUser(id);
    }

    public void deleteUser(UUID id) {
        userDao.deleteUser(id);
    }

    public Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields) {
        return userDao.updateUser(id, fields);
    }
}
