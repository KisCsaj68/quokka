package com.codecool.quokka.controller;

import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import com.codecool.quokka.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/api/v1/user")
@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping
    public Set<UserDto> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping(path = "{id}")
    public UserDto getUserById(@PathVariable("id") UUID id) {
        return userService.getUser(id).orElse(null);
    }

    @DeleteMapping
    public void deleteUserById(@RequestBody User user) {
        userService.deleteUser(user.getId());
    }
}
