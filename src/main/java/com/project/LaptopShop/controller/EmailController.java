package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.service.EmailService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final UserService userService;
    private final EmailService emailService;

    public static boolean isEmail(String input) {
        return EMAIL_PATTERN.matcher(input).matches();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<User> getMethodName(@RequestBody Map<String, Object> userMap) throws IdInvalidException {
        String user = (String) userMap.get("user");
        // Kiểm tra xem có phải là email
        String re = isEmail(user) ? "email" : "user";
        User userDB = new User();
        if (re == "user") {
            userDB = this.userService.getUserByUserNameAndType(user, TypeEnum.SYSTEM);
        } else {
            userDB = this.userService.getUserByEmailAndType(user, TypeEnum.SYSTEM);
        }
        if (userDB == null) {
            throw new IdInvalidException("User not found");
        }
        String uuid = UUID.randomUUID().toString();
        userDB.setCode(uuid);
        userDB.setExpiredAt(Instant.now());
        this.userService.save(userDB);
        this.emailService.sendMailForgetPasswordAndActivePassword(userDB.getEmail(), "Reset password", "reset-password",
                uuid,
                userDB.getEmail());
        // Trả về một đối tượng JSON hợp lệ
        return ResponseEntity.ok(userDB);
    }

}
