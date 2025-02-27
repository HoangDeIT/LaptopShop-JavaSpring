package com.project.LaptopShop.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.StatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = { "orders", "carts" }, allowSetters = true)
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "order" })
    private List<OrderDetail> orderDetails;
    private double totalPrice;
    @NotBlank
    private String receiverAddress;
    @NotBlank
    private String receiverPhone;
    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.PENDING;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted = false;
    private Instant deletedAt;

    @PrePersist
    public void handleBeforeCreate() {
        createdBy = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin() : "unknown";
        createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        updatedBy = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin() : "unknown";
        updatedAt = Instant.now();
    }
}
