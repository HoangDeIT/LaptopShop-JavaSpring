
package com.project.LaptopShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "cart")
public class Cart {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @ManyToOne
        @JoinColumn(name = "product_id")
        @JsonIgnoreProperties(value = { "carts", "images", "createdAt", "updatedAt", "createdBy", "updatedBy",
                        "deleted",
                        "deletedAt" })
        private Product product;
        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonIgnoreProperties(value = { "carts", "orders", "createdAt", "updatedAt", "createdBy", "updatedBy",
                        "deleted",
                        "deletedAt" })
        private User user;
        private int quantity;

}
