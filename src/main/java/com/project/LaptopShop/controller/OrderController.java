package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Order;
import com.project.LaptopShop.domain.OrderDetail;
import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.OrderService;
import com.project.LaptopShop.service.ProductService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    public OrderController(UserService userService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws IdInvalidException {
        if (order.getOrderDetails().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String username = SecurityUtil.getCurrentUserLogin();
        User user = this.userService.getUserByUsername(username);
        double totalPrice = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            long id = orderDetail.getProduct().getId();
            Product product = this.productService.getProductById(id);
            orderDetail.setProduct(product);
            totalPrice += product.getPrice() * orderDetail.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        order.setUser(user);
        Order entity = this.orderService.createOrder(order);
        return ResponseEntity.ok(entity);
    }
    // @PostMapping
    // public String postMethodName() {
    // //TODO: process POST request

    // return entity;
    // }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> postMethodName(Pageable pageable, @Filter Specification<Order> spec) {

        return ResponseEntity.ok(this.orderService.fetchOrder(pageable, spec));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathParam("id") long id) {
        this.orderService.deleteOrder(id);
        return ResponseEntity.ok().body(null);
    }
}
