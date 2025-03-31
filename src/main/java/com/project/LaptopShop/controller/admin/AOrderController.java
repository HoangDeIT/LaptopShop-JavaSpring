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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Cart;
import com.project.LaptopShop.domain.Order;
import com.project.LaptopShop.domain.OrderDetail;
import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.request.OrderDTO;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.CartService;
import com.project.LaptopShop.service.OrderService;
import com.project.LaptopShop.service.ProductService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.StatusEnum;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AOrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CartService cartService;

    public AOrderController(UserService userService, OrderService orderService, ProductService productService,
            CartService cartService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.cartService = cartService;
    }

    // @PostMapping
    // public String postMethodName() {
    // //TODO: process POST request

    // return entity;
    // }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> postMethodName(Pageable pageable, @Filter Specification<Order> spec) {

        return ResponseEntity.ok(this.orderService.fetchOrderAdmin(pageable, spec));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathParam("id") long id) throws IdInvalidException {
        this.orderService.deleteOrder(id);
        return ResponseEntity.ok().body(null);
    }

    @PatchMapping
    public ResponseEntity<Order> updateStatus(@RequestParam("id") long id, @RequestParam("action") StatusEnum action)
            throws IdInvalidException {
        Order order = this.orderService.fetchOrderById(id);
        order.setStatus(action);
        return ResponseEntity.ok(this.orderService.createOrder(order));
    }

    @GetMapping("/count-pending-order")
    public long getMethodName() {
        return this.orderService.pendingCount();
    }

    @PostMapping("/rollback-delete")
    public ResponseEntity<Order> postMethodName(@PathParam("id") long id) throws IdInvalidException {

        return ResponseEntity.ok(this.orderService.rollbackOrder(id));
    }

}
