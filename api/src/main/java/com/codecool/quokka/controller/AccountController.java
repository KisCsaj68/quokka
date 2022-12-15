package com.codecool.quokka.controller;

import com.codecool.quokka.model.account.Account;

import com.codecool.quokka.model.account.AccountDto;
import com.codecool.quokka.service.account.AccountService;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/user")
@RestController
public class AccountController {

    private static final Counter user_request = Counter.build().namespace("quokka").subsystem("api")
            .name("user_request")
            .labelNames("operation")
            .help("total number of user operations").register();

    private static final Histogram user_request_time_duration = Histogram.build().namespace("quokka").subsystem("api")
            .name("user_request_time_duration")
            .labelNames("operation", "controller")
            .help("total elapsed time from request to response").register();

    private static final String a = user_request_time_duration.describe().get(0).name;
    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity addUser(@RequestBody Account account) {
        user_request.labels("create").inc();
        Histogram.Timer timer = user_request_time_duration.labels("create", "operation").startTimer();
        try {
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
        } finally {
            timer.observeDuration();
        }
    }

    @GetMapping
    // admin endpoint
    public Set<AccountDto> getAllUser() {
        user_request.labels("read_all").inc();
        Histogram.Timer timer = user_request_time_duration.labels("read_all").startTimer();
        try {
            return accountService.getAllAccount();
        } finally {
            timer.observeDuration();
        }
    }

    @GetMapping(path = "{id}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity getUserById(@PathVariable("id") UUID id) {
        user_request.labels("read").inc();
        Histogram.Timer timer = user_request_time_duration.labels("read").startTimer();
        try {
            Optional<AccountDto> account = accountService.getAccount(id);
            if (account.isPresent()) {
                return new ResponseEntity<>(account.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } finally {
            timer.observeDuration();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity deleteUserById(@RequestBody HashMap<String, String> body) {
        user_request.labels("delete").inc();
        Histogram.Timer timer = user_request_time_duration.labels("delete").startTimer();
        try {
            UUID id = UUID.fromString(body.get("id"));
            if (accountService.getAccount(id).isPresent()) {
                accountService.deleteAccount(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } finally {
            timer.observeDuration();
        }
    }

    @PutMapping(path = "{id}")
    @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity updateUser(@PathVariable("id") UUID id, @RequestBody HashMap<String, String> fields) {
        user_request.labels("update").inc();
        Histogram.Timer timer = user_request_time_duration.labels("read").startTimer();
        try {
            Optional<AccountDto> accountDto = accountService.updateAccount(id, fields);
            if (accountDto.isPresent()) {
                return new ResponseEntity<>(accountDto.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } finally {
            timer.observeDuration();
        }
    }
}
