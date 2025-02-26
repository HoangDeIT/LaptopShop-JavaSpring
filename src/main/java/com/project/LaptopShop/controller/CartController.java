package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Cart;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.service.CartService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.error.IdInvalidException;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final UserService userService;
    private final CartService cartService;

    public CartController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    // @GetMapping
    // public ResponseEntity<List<Cart>> getCart() {
    // String username = SecurityUtil.getCurrentUserLogin(); //SecurityUtil
    // User user=this.userService.getUserByUsername(username);
    // if(user!=null){
    // return ResponseEntity.ok(this.cartService.getCartByUser(user));
    // }
    // return new String();
    // }
    @PostMapping
    public String postMethodName(@RequestParam("id") long id,
            @RequestParam("quantity") int quantity) throws IdInvalidException {
        String username = SecurityUtil.getCurrentUserLogin(); // SecurityUtil
        User user = this.userService.getUserByUsername(username);
        if (user != null) {
            this.cartService.addProductToCart(user, id, quantity);
        }
        String entity = "OK";
        return entity;
    }

    @PostMapping("/update")
    public String postMethodName(@RequestBody User user) throws IdInvalidException {
        String username = SecurityUtil.getCurrentUserLogin();
        User userDB = this.userService.getUserByUsername(username);
        if (userDB != null) {
            this.cartService.updateProductToCart(userDB, user);
        }
        String entity = "OK";
        return entity;
    }

}
