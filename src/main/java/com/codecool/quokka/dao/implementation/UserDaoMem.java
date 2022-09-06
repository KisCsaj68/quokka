package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserData;
import com.google.gson.Gson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {

    private static Set<User> DB = new HashSet<>();
    private Gson gson = new Gson();
    @Override
    public String addUser(String name, String userName, String emailAddress, String passWord) {
        User newUser = new User(name, emailAddress, userName, passWord);
        DB.add(newUser);

        return gson.toJson(new UserData(newUser.getUserName(), newUser.getId().toString()));
    }

    @Override
    public String addUser(User user) {
        DB.add(user);
        return gson.toJson(new UserData(user.getUserName(), user.getId().toString()));
    }
}
