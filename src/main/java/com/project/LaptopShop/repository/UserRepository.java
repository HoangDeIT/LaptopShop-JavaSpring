package com.project.LaptopShop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.util.constant.TypeEnum;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameAndType(String email, TypeEnum type);
}