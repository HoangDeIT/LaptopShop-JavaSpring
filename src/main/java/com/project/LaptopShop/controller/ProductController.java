package com.project.LaptopShop.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.ProductService;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getMethodName(@Filter Specification<Product> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.productService.fetchProduct(pageable, spec));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) throws IdInvalidException {
        return ResponseEntity.ok().body(this.productService.getProductById(id));
    }
}
