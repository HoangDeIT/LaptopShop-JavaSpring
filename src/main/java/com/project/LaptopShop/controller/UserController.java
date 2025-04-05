package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.service.FileService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.TypeEnum;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<User> getHello() {
        String username = SecurityUtil.getCurrentUserLogin();
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User user = this.userService.getUserByUserNameAndType(username, type);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<User> postMethodName(@RequestParam(value = "file", required = false) MultipartFile file)
            throws URISyntaxException, IOException {
        // User entity = this.userService.findById(id);
        String username = SecurityUtil.getCurrentUserLogin();
        User user = this.userService.getUserByUserNameAndType(username, TypeEnum.SYSTEM);
        this.fileService.createUploadFolder("avatar");
        String name = this.fileService.store(file, "avatar");
        user.setImage(name);
        return ResponseEntity.ok(this.userService.save(user));
    }

}
