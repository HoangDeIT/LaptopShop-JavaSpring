package com.project.LaptopShop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.ProductImages;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Long>, JpaSpecificationExecutor<Product> {

}
