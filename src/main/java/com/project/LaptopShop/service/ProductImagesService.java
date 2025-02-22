package com.project.LaptopShop.service;

import org.springframework.stereotype.Service;

import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.ProductImages;
import com.project.LaptopShop.repository.ProductImagesRepository;

@Service
public class ProductImagesService {
    private final ProductImagesRepository productImagesRepository;

    public ProductImagesService(ProductImagesRepository productImagesRepository) {
        this.productImagesRepository = productImagesRepository;
    }

    public ProductImages saveImageProduct(String name, Product product) {
        ProductImages productImages = new ProductImages();
        productImages.setProduct(product);
        productImages.setImage(name);
        return this.productImagesRepository.save(productImages);
    }
}
