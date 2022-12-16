package com.codecool.quokka.controller;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private static final Counter login_request = Counter.build().namespace("quokka").subsystem("api")
            .name("login_request")
            .help("total number of login operations").register();

    @PostMapping(path = "login")
    public void login() {
        login_request.inc();
    }
}
