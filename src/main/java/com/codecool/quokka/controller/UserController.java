package com.codecool.quokka.controller;

import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import com.codecool.quokka.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1/user")
@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public @ResponseBody UserDto addUser(@RequestBody User user) {
        System.out.println(user);
        return userService.addUser(user);
    }

    @GetMapping
    public Set<UserDto> getAllUser(){
        return userService.getAllUser();
    }
}
