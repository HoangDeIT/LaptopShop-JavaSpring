package com.project.LaptopShop.domain;

import java.time.Instant;

import com.project.LaptopShop.util.SecurityUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "product_images")
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Image is required")
    private String image;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
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
