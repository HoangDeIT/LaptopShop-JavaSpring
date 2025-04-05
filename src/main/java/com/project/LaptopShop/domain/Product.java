package com.project.LaptopShop.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.TypeLaptopEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "Price phải lớn hơn 0")
    private double price;
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "product", "createdAt", "updatedAt", "createdBy", "updatedBy", "deleted",
            "deletedAt" })
    private List<ProductImages> images;
    private String mainImage;
    @Column(columnDefinition = "MEDIUMTEXT")
    @NotBlank(message = "Detail description is required")
    private String detailDesc;

    @NotBlank
    @NotBlank(message = "Không được để trống")
    private String shortDesc;

    @Min(value = 0, message = "Số lượng phải lớn 0")
    private long quantity;
    private long sold;
    @ManyToOne
    @JoinColumn(name = "factory_id")
    @JsonIgnoreProperties(value = { "products", "createdAt", "updatedAt", "createdBy", "updatedBy", "deleted",
            "deletedAt" })
    private Factory factory;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeLaptopEnum type;
    @NotBlank
    private String cpu;
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "Rom phải lớn hơn 0")
    private float rom;
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "Ram phải lớn hơn 0")
    private float ram;
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "Screen phải lớn hơn 0")
    private float screen;
    @NotBlank
    private String os;
    @NotBlank
    private String gpu;

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
