package com.project.LaptopShop.domain.request;

import java.util.List;

import com.project.LaptopShop.domain.Cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDTO {
    @NotBlank
    private String receiverAddress;
    @NotBlank
    private String receiverPhone;
    private List<Cart> carts;
}
