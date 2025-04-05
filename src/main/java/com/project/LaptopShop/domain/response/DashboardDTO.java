package com.project.LaptopShop.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDTO {

    private double totalUser;
    private double totalOrder;
    private double totalPrice;
    private double totalOrderPending;
}
