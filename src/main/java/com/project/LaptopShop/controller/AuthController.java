package com.project.LaptopShop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.request.SocialNetworkLoginDTO;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.domain.response.ResLoginDTO;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.constant.RoleEnum;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.SecurityUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    @Value("${password.socical-network}")
    private String passwordSocialNetwork;
    private final SecurityUtil securityUtil;

    public AuthController(PasswordEncoder passwordEncoder, UserService userService,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> register(@Valid @RequestBody User user) {
        user.setRole(RoleEnum.USER);
        user.setType(TypeEnum.SYSTEM);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody JsonNode user) {
        String userName = user.get("username").asText();
        String password = user.get("password").asText();
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password);
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create token

        User currentUser = this.userService.getUserByUserNameAndType(userName, TypeEnum.SYSTEM);
        String access_token = this.securityUtil.createAccessToken(authentication, currentUser);
        String refresh_token = this.securityUtil.createRefreshToken(authentication, currentUser);
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setAccessToken(access_token);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setEmail(currentUser.getEmail());
        userLogin.setId(currentUser.getId());
        userLogin.setUserName(userName);
        userLogin.setRole(currentUser.getRole());
        resLoginDTO.setUser(userLogin);
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/social-network")
    public ResponseEntity<ResLoginDTO> postMethodName(@RequestBody SocialNetworkLoginDTO socialNetworkLoginDTO) {
        TypeEnum type;
        if (socialNetworkLoginDTO.getType().equals("GOOGLE")) {
            type = TypeEnum.GOOGLE;
        } else {
            type = TypeEnum.GITHUB;
        }
        User user = this.userService.getUserByUserNameAndType(socialNetworkLoginDTO.getName(), type);
        boolean isNewUser = false;
        if (user == null) {
            user = new User();
            user.setEmail(socialNetworkLoginDTO.getEmail());
            user.setPassword(passwordSocialNetwork);
            user.setRole(RoleEnum.USER);
            user.setType(type);
            user.setUserName(socialNetworkLoginDTO.getName());
            user.setImage(socialNetworkLoginDTO.getImage());
            this.userService.registerUser(user);
            isNewUser = true;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                socialNetworkLoginDTO.getName(), passwordSocialNetwork);
        // Set thông tin Authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create token (COPY CODE FROM LOGIN)
        User currentUser = user;
        String access_token = this.securityUtil.createAccessToken(authentication, currentUser);
        String refresh_token = this.securityUtil.createRefreshToken(authentication, currentUser);
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setAccessToken(access_token);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setEmail(currentUser.getEmail());
        userLogin.setId(currentUser.getId());
        userLogin.setUserName(currentUser.getUserName());
        userLogin.setRole(currentUser.getRole());
        resLoginDTO.setUser(userLogin);
        return ResponseEntity.status(isNewUser ? HttpStatus.CREATED : HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(resLoginDTO);
    }

}
