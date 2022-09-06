package com.codecool.quokka.dao;

import com.codecool.quokka.model.User;

import java.util.List;

public interface UserDao {

     String addUser(String name, String userName, String emailAddress, String passWord);

     String addUser(User user);
}
