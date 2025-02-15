package com.project.LaptopShop.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.constant.TypeEnum;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/admin/user")
public class AUserController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AUserController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RegisterDTO> createUser(@Valid @RequestBody User user) {
        user.setType(TypeEnum.SYSTEM);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.registerUser(user));
    }

}
