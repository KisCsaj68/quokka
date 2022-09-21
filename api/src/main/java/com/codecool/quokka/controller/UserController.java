package com.codecool.quokka.controller;

import com.codecool.quokka.model.user.User;

import com.codecool.quokka.model.user.UserDto;
import com.codecool.quokka.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/v1/user")
@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public UserDto addUser(@RequestBody User user) {
        System.out.println(user);
        return userService.addUser(user);
    }

    @GetMapping
    public Set<UserDto> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        if (userService.getUser(id).isPresent()){
            return new ResponseEntity<>(userService.getUser(id).get(), HttpStatus.OK) ;
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public void deleteUserById(@RequestBody HashMap<String, String> body) {
        userService.deleteUser(UUID.fromString(body.get("id")) );
    }

    @PutMapping(path = "{id}")
    public UserDto updateUser(@PathVariable("id") UUID id, @RequestBody HashMap<String, String> fields){
        return userService.updateUser(id, fields).orElse(null);

    }
}
