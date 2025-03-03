package com.project.LaptopShop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Cart;
import com.project.LaptopShop.domain.OrderDetail;
import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.service.CartService;
import com.project.LaptopShop.service.ProductService;
import com.project.LaptopShop.service.UserService;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

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
    private final ProductService productService;

    public CartController(UserService userService,
            CartService cartService,
            ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
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
    public ResponseEntity<Void> postMethodName(@RequestParam("id") long id,
            @RequestParam("quantity") int quantity) throws IdInvalidException {
        String username = SecurityUtil.getCurrentUserLogin(); // SecurityUtil
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User user = this.userService.getUserByUserNameAndType(username, type);
        if (user != null) {
            this.cartService.addProductToCart(user, id, quantity);
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> postMethodName(@RequestBody User user) throws IdInvalidException {
        String username = SecurityUtil.getCurrentUserLogin();
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User userDB = this.userService.getUserByUserNameAndType(username, type);
        if (userDB != null) {
            this.cartService.updateProductToCart(userDB, user);
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> postMethodName(@RequestParam("id") long id) throws IdInvalidException {

        return ResponseEntity.ok(this.cartService.addOneQuantityCart(id));
    }

    @PostMapping("/sub")
    public ResponseEntity<Cart> subCart(@RequestParam("id") long id) throws IdInvalidException {

        return ResponseEntity.ok(this.cartService.subOneQuantityCart(id));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCart(@RequestParam("id") long id) throws IdInvalidException {
        this.cartService.deleteCart(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/get-cart")
    public ResponseEntity<List<Cart>> getMethodName(@RequestParam(value = "id", required = false) List<String> ListId)
            throws IdInvalidException {

        try {
            // Chuyển đổi danh sách String thành danh sách Long sử dụng Stream

            if (ListId != null && !ListId.isEmpty()) {
                List<Long> longList = ListId.stream()
                        .map(Long::parseLong) // Chuyển mỗi String thành Long
                        .collect(Collectors.toList()); // Thu thập lại thành danh sách
                if (this.cartService.isCartInUser(longList)) {
                    List<Cart> cartDetails = this.cartService.getCart(longList);
                    return ResponseEntity.ok(cartDetails);
                }

            }
            String username = SecurityUtil.getCurrentUserLogin();
            TypeEnum type = SecurityUtil.getCurrentUserType();
            User user = this.userService.getUserByUserNameAndType(username, type);
            return ResponseEntity.ok(user.getCarts());

        } catch (NumberFormatException e) {
            System.out.println("Lỗi khi chuyển đổi chuỗi thành số Long: " +
                    e.getMessage());
        }
        return null;
    }

    @GetMapping("/buy-now")
    public ResponseEntity<List<Cart>> getMethodName(@RequestParam("productID") String productID) {
        try {
            long id = Long.parseLong(productID);
            Product product = this.productService.getProductById(id);
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setQuantity(1);
            List<Cart> listCart = List.of(cart);
            return ResponseEntity.ok(listCart);
        } catch (Exception e) {
            return null;
        }
    }
}
