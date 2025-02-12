package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/")
    public String getHello() {
        return "Hello";
    }

}
