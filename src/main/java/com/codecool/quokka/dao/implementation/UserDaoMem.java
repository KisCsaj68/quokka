package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import com.google.gson.Gson;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {

    private static Set<User> DB = new HashSet<>();
    private Gson gson = new Gson();
    @Override
    public UserDto addUser(String name, String userName, String emailAddress, String passWord) {
        User newUser = new User(name, emailAddress, userName, passWord);
        DB.add(newUser);
        return new UserDto(userName,newUser.getId());
    }

    @Override
    public UserDto addUser(User user) {
        DB.add(user);
        return new UserDto(user.getUserName(), user.getId());
    }
}
