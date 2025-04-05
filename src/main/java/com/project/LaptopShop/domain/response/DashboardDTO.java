package com.project.LaptopShop.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class DashboardDTO {

    private double totalUser;
    private double totalOrder;
    private double totalPrice;
    private double totalOrderPending;
}
