package com.project.LaptopShop.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Order;
import com.project.LaptopShop.domain.OrderDetail;

import com.project.LaptopShop.domain.response.EmailOrder;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.EmailService;
import com.project.LaptopShop.service.OrderService;

import com.project.LaptopShop.util.constant.StatusEnum;

import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/orders")
@AllArgsConstructor
public class AOrderController {

    private final OrderService orderService;

    private final EmailService emailService;

    // @PostMapping
    // public String postMethodName() {
    // //TODO: process POST request

    // return entity;
    // }
    public EmailOrder OrderToEmailOrder(Order order) {
        EmailOrder emailOrder = new EmailOrder();
        emailOrder.setAddress(order.getReceiverAddress());
        emailOrder.setPhone(order.getReceiverPhone());
        emailOrder.setName(order.getUser().getUserName());
        emailOrder.setTotal(order.getTotalPrice());
        emailOrder.setMessage(order.getMessage());
        List<EmailOrder.OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            EmailOrder.OrderDetail emailOrderDetail = new EmailOrder.OrderDetail();
            emailOrderDetail.setProduct(orderDetail.getProduct().getName());
            emailOrderDetail.setPrice(orderDetail.getProduct().getPrice());
            emailOrderDetail.setQuantity(orderDetail.getQuantity());
            orderDetails.add(emailOrderDetail);
        }
        emailOrder.setOrderDetails(orderDetails);
        return emailOrder;
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> postMethodName(Pageable pageable, @Filter Specification<Order> spec) {

        return ResponseEntity.ok(this.orderService.fetchOrderAdmin(pageable, spec));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable("id") long id) throws IdInvalidException {
        this.orderService.deleteOrder(id);
        return ResponseEntity.ok().body(null);
    }

    @PatchMapping
    public ResponseEntity<Order> updateStatus(@RequestParam("id") long id, @RequestParam("action") StatusEnum action,
            @RequestParam(name = "message", required = false) String message)
            throws IdInvalidException {
        Order order = this.orderService.fetchOrderById(id);

        order.setStatus(action);
        if (action.equals(StatusEnum.REFUSED)) {
            order.setMessage(message != null && !message.isEmpty() ? message : "Đơn hàng không hợp lệ");
            this.emailService.sendMessageOrder(order.getUser().getEmail(), "Đơn hàng bị từ chối", "refused",
                    OrderToEmailOrder(order));
        } else if (action.equals(StatusEnum.DELIVERED)) {
            this.emailService.sendMessageOrder(order.getUser().getEmail(), "Đơn hàng đã được giao", "delivered",
                    OrderToEmailOrder(order));
        }
        return ResponseEntity.ok(this.orderService.createOrder(order));
    }

    @GetMapping("/count-pending-order")
    public long getMethodName() {
        return this.orderService.pendingCount();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Order> postMethodName(@PathVariable("id") long id) throws IdInvalidException {

        return ResponseEntity.ok(this.orderService.rollbackOrder(id));
    }

}
