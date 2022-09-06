package com.codecool.quokka.dao;

import com.codecool.quokka.model.User;

public interface UserDao {

     User addUser(String name, String userName, String emailAddress, String passWord);
}
