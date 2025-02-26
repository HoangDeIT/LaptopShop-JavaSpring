package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getHello() {
        String username = SecurityUtil.getCurrentUserLogin();
        User user = this.userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

}
