package com.project.LaptopShop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.repository.UserRepository;
import com.project.LaptopShop.util.constant.TypeEnum;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {

        Optional<User> userOptional = this.userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public RegisterDTO registerUser(User user) {
        user = this.userRepository.save(user);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setId(user.getId());
        registerDTO.setUserName(user.getUserName());
        registerDTO.setEmail(user.getEmail());
        registerDTO.setCreatedAt(user.getCreatedAt());
        registerDTO.setCreatedBy(user.getCreatedBy());
        registerDTO.setRole(user.getRole());
        return registerDTO;
    }

    public User getUserByUserName(String username) {
        Optional<User> userOptional = this.userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User getUserByUserNameAndType(String username, TypeEnum type) {
        Optional<User> userOptional = this.userRepository.findByUserNameAndType(username, type);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }
}
