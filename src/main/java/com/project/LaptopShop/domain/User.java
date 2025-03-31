package com.project.LaptopShop.domain;

import java.time.Instant;
import java.util.List;

import com.project.LaptopShop.util.constant.RoleEnum;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.LaptopShop.util.SecurityUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userName;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonIgnore(value = true)
    private String password;
    @NotBlank(message = "Password is required")
    private String email;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    private String image;
    @Enumerated(EnumType.STRING)
    private TypeEnum type;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "user", "createdAt", "updatedAt", "createdBy", "updatedBy", "deleted",
            "deletedAt" })
    private List<Cart> carts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "user", "createdAt", "updatedAt", "createdBy", "updatedBy", "deleted",
            "deletedAt" })
    private List<Order> orders;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted = false;
    private Instant deletedAt;
    private String code;
    private Instant expiredAt;
    private boolean isActive;

    @PrePersist
    public void handleBeforeCreate() {
        createdBy = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin() : userName;
        createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        updatedBy = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin() : userName;
        updatedAt = Instant.now();
    }
}
