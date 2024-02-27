package com.example.saml2demo.controllers;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class MainController {
    

    @GetMapping("/")
    public Object getMethodName() {
        return Map.of("name", "Ramdane");
    }
    
}
