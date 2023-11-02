package com.web.app.controller;


import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@AllArgsConstructor
public class WebRestController {

    private Environment env;


    @GetMapping("/port")
    public String getPORT() {
        return env.getProperty("local.server.port");
    }

    @GetMapping("/ip")
    public String getIP() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();
        return ip;
    }
}
