package com.codecool.quokka.service;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
}
