package com.project.LaptopShop.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.response.DashboardDTO;
import com.project.LaptopShop.service.OrderService;
import com.project.LaptopShop.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin/dashboard")
public class ADashboardController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getMethodName() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setTotalOrder(orderService.getTotalRevenue());
        dashboardDTO.setTotalOrderPending(orderService.pendingCount());
        dashboardDTO.setTotalPrice(orderService.getTotalRevenue());
        dashboardDTO.setTotalUser(userService.getTotalUser());
        return ResponseEntity.ok(dashboardDTO);
    }

}
