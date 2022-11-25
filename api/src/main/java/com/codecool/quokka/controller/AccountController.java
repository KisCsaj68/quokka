package com.codecool.quokka.controller;

import com.codecool.quokka.model.account.Account;

import com.codecool.quokka.model.account.AccountDto;
import com.codecool.quokka.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity addUser(@RequestBody Account account) {
        if (accountService.getAccountByEmail(account.getEmailAddress())) {
            return new ResponseEntity<>("Email occupied", HttpStatus.BAD_REQUEST);
        }
        if (accountService.getAccountByUserName(account.getUserName())) {
            return new ResponseEntity<>("User error", HttpStatus.BAD_REQUEST);
        }
        if (!accountService.validate(account.getEmailAddress())) {
            return new ResponseEntity<>("Email error", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(accountService.addAccount(account), HttpStatus.OK);
    }

    @GetMapping
    // admin endpoint
    public Set<AccountDto> getAllUser() {
        return accountService.getAllAccount();
    }

    @GetMapping(path = "{id}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity getUserById(@PathVariable("id") UUID id) {
        if (accountService.getAccount(id).isPresent()) {
            return new ResponseEntity<>(accountService.getAccount(id).get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public void deleteUserById(@RequestBody HashMap<String, String> body) {
        accountService.deleteAccount(UUID.fromString(body.get("id")));
    }

    @PutMapping(path = "{id}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public AccountDto updateUser(@PathVariable("id") UUID id, @RequestBody HashMap<String, String> fields) {
        return accountService.updateAccount(id, fields).orElse(null);
    }
}
