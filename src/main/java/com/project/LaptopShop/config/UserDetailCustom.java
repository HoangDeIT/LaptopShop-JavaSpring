package com.project.LaptopShop.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.constant.TypeEnum;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {

    private final UserService userService;

    public UserDetailCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        com.project.LaptopShop.domain.User user = this.userService.getUserByUserNameAndType(username,
                TypeEnum.SYSTEM);
        System.out.println("User: " + user.getUserName() + ", Role: " + user.getRole().name());

        return new User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

}
