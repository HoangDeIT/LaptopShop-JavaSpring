package com.project.LaptopShop.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.util.constant.TypeEnum;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserName(String userName);

    List<User> findByDeletedTrueAndDeletedAtBefore(Instant cutoffTime);

    List<User> findByDeletedFalse(Pageable page, Specification<User> spec);

    Optional<User> findByUserNameAndType(String email, TypeEnum type);
}