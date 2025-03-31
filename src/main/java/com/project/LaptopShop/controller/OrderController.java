package com.project.LaptopShop.controller;

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
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    public OrderController(UserService userService, OrderService orderService, ProductService productService,
            CartService cartService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.cartService = cartService;
    }

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CartService cartService;

    @PostMapping
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO,
            @RequestHeader("type") String typeCart) throws IdInvalidException {
        if (orderDTO.getCarts() == null || orderDTO.getCarts().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Order order = new Order();
        order.setReceiverAddress(orderDTO.getReceiverPhone());
        order.setReceiverPhone(orderDTO.getReceiverAddress());
        String username = SecurityUtil.getCurrentUserLogin();
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User user = this.userService.getUserByUserNameAndType(username, type);
        double totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
        for (Cart cart : orderDTO.getCarts()) {
            long id = cart.getProduct().getId();
            Product product = this.productService.getProductById(id);
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cart.getQuantity());
            orderDetails.add(orderDetail);
            totalPrice += product.getPrice() * cart.getQuantity();
        }
        order.setOrderDetails(orderDetails);
        order.setTotalPrice(totalPrice);
        order.setUser(user);

        Order entity = this.orderService.createOrder(order);
        if (!typeCart.equals("buy-now")) {
            for (Cart cart : orderDTO.getCarts()) {

                user.getCarts().remove(this.cartService.getCartById(cart.getId()));
            }
        }
        this.userService.save(user);
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
    public ResponseEntity<Void> deleteCart(@PathParam("id") long id) throws IdInvalidException {
        this.orderService.deleteOrder(id);
        return ResponseEntity.ok().body(null);
    }
}
