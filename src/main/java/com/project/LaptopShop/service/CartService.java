package com.project.LaptopShop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.project.LaptopShop.domain.Cart;
import com.project.LaptopShop.domain.Factory;
import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.repository.CartRepository;
import com.project.LaptopShop.repository.FactoryRepository;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    public CartService(FilterSpecificationConverter filterSpecificationConverter, FilterParser filterParser,
            CartRepository cartRepository, ProductService productService, UserService userService) {
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterParser = filterParser;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
    }

    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterParser filterParser;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;

    public ResultPaginationDTO fetchOrder(Pageable pageable, Specification<Cart> spec, User user) {

        FilterNode node = filterParser.parse("deleted='" + false + "'");
        FilterSpecification<Cart> spec1 = filterSpecificationConverter.convert(node);
        spec = spec.and(spec1);
        Page<Cart> pageFactory = this.cartRepository.findAll(spec, pageable);

        // Điều chỉnh số trang nếu vượt quá tổng số trang
        int totalPages = pageFactory.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber()) {
            pageable = pageable.withPage(totalPages - 1);
            pageFactory = this.cartRepository.findAll(spec, pageable);
        }

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageFactory.getTotalPages());
        meta.setTotal(pageFactory.getTotalElements());
        res.setMeta(meta);

        res.setResult(pageFactory.getContent());
        return res;
    }

    public void addProductToCart(User user, long id, int quantity) throws IdInvalidException {
        List<Cart> carts = user.getCarts();
        boolean found = carts.stream().anyMatch(cart -> cart.getProduct().getId() == id);
        if (!found) {
            Cart cart = new Cart();
            Product product = this.productService.getProductById(id);
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(quantity);
            this.cartRepository.save(cart);
        } else {
            Cart cart = user.getCarts().stream().filter(cart1 -> cart1.getProduct().getId() == id).findFirst().get();
            cart.setQuantity(cart.getQuantity() + quantity);
            this.cartRepository.save(cart);
        }

    }

    @Transactional
    public void updateProductToCart(User userDB, User user) throws IdInvalidException {
        // User userDB = this.userService.getUserByUsername(username);

        List<Cart> carts = userDB.getCarts();
        List<Cart> cartsUpdate = user.getCarts();
        for (Cart cart : carts) {
            boolean found = cartsUpdate.stream()
                    .anyMatch(cart1 -> cart1.getId() == cart.getId());
            if (!found) {
                // no cart
                userDB.getCarts().remove(cart);
                this.cartRepository.deleteById(cart.getId());
            } else {
                if (cart.getQuantity() <= 0) {
                    userDB.getCarts().remove(cart);
                    this.cartRepository.deleteById(cart.getId());
                } else {
                    Cart cartUpdate = user.getCarts().stream()
                            .filter(cart1 -> cart1.getId() == cart.getId()).findFirst().get();
                    cart.setQuantity(cartUpdate.getQuantity());
                    this.cartRepository.save(cart);
                }
            }

        }
    }

    public Cart addOneQuantityCart(long id) throws IdInvalidException {
        Cart cart = this.cartRepository.findById(id).get();
        cart.setQuantity(cart.getQuantity() + 1);
        return this.cartRepository.save(cart);
    }

    @Transactional
    public Cart subOneQuantityCart(long id) throws IdInvalidException {
        Cart cart = this.cartRepository.findById(id).get();
        cart.setQuantity(cart.getQuantity() - 1);
        if (cart.getQuantity() <= 0) {
            User user = cart.getUser();
            user.getCarts().remove(cart);
            // this.cartRepository.deleteById(id);
        }
        return this.cartRepository.save(cart);
    }

    public void deleteCart(long id) throws IdInvalidException {
        Cart cart = this.cartRepository.findById(id).get();
        User user = cart.getUser();
        user.getCarts().remove(cart);
        this.cartRepository.save(cart);

    }

    public List<Cart> getCart(List<Long> ids) throws IdInvalidException {
        return this.cartRepository.findAllById(ids);
    }

    public boolean isCartInUser(List<Long> ids) {
        String username = SecurityUtil.getCurrentUserLogin();
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User user = this.userService.getUserByUserNameAndType(username, type);
        List<Long> idsUser = user.getCarts().stream().map(Cart::getId).toList();
        return idsUser.containsAll(ids);
    }

    public Cart getCartById(long id) throws IdInvalidException {
        return this.cartRepository.findById(id).get();
    }
}
