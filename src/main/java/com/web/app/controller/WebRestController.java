package com.web.app.controller;


import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WebRestController {

    private Environment env;


    @GetMapping("/port")
    public String getProfile() {
        return env.getProperty("local.server.port");
    }
}
