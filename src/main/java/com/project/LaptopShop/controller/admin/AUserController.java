package com.project.LaptopShop.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.domain.response.ResUserDTO;
import com.project.LaptopShop.domain.response.RestResponse;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getMethodName(@Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.fetchUser(pageable, spec));
    }

    @PatchMapping
    public ResponseEntity<ResUserDTO> postMethodName(@RequestBody User user) {
        if (user == null || user.getId() == 0 || user.getRole() == null) {
            return null;
        }
        return ResponseEntity.ok().body(this.userService.changeRole(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResUserDTO> rollBack(@PathVariable("id") long id) {
        if (id == 0)
            return null;
        return ResponseEntity.ok().body(this.userService.rollbackDelete(id));
    }

}
