package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {

    private static Set<User> DB = new HashSet<>();

    @Override
    public User addUser(String name, String userName, String emailAddress, String passWord) {
        User newUser = new User(name, emailAddress, userName, passWord);
        DB.add(newUser);
        return newUser;
    }
}
