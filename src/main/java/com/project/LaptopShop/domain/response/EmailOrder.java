package com.project.LaptopShop.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class EmailOrder {

    private String message;
    private String phone;
    private String address;
    private String name;
    private double total;
    private List<OrderDetail> orderDetails;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetail {
        private String product;
        private int quantity;
        private double price;
    }

}
