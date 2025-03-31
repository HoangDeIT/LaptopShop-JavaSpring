package com.project.LaptopShop.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.ProductImages;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.service.FileService;
import com.project.LaptopShop.service.ProductImagesService;
import com.project.LaptopShop.service.ProductService;

import com.project.LaptopShop.util.error.IdInvalidException;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/admin/product")
public class AProductController {
    private final FileService fileService;
    private final ProductService productService;
    private final ProductImagesService productImagesService;

    public AProductController(FileService fileService, ProductService productService,
            ProductImagesService productImagesService) {
        this.fileService = fileService;
        this.productService = productService;
        this.productImagesService = productImagesService;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Product> createProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("id") long id) throws URISyntaxException, IOException, IdInvalidException {
        Product product = this.productService.getProductById(id);
        this.fileService.createUploadFolder("product");
        String fileName = this.fileService.store(file, "product");
        List<String> fileNames = this.fileService.storeMultipleFiles(files, "product");
        List<ProductImages> imagesDB = product.getImages();
        imagesDB.clear();
        for (String fileName1 : fileNames) {
            ProductImages productImages = this.productImagesService.saveImageProduct(fileName1, product);
            imagesDB.add(productImages);
        }
        // imagesDB =
        // fileNames.stream().map(this.productImagesService::saveImageProduct)
        // .collect(Collectors.toList());
        product.setMainImage(fileName);
        product.setImages(imagesDB);
        // fix to list
        // product.setImages(new ArrayList<>(productImages));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.saveProduct(product));
    }

    @PostMapping
    public ResponseEntity<Product> createUser(@Valid @RequestBody Product product) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.saveProduct(product));
    }

    @PatchMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.productService.saveProduct(product));
    }

    @PatchMapping("/upload-image")
    public ResponseEntity<Product> updateProductImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam("id") long id) throws URISyntaxException, IOException, IdInvalidException {
        Product product = this.productService.getProductById(id);
        this.fileService.createUploadFolder("product");
        if (file != null && !file.isEmpty()) {
            String fileName = this.fileService.store(file, "product");
            product.setMainImage(fileName);
        }
        if (files != null && !files.isEmpty()) {
            List<String> fileNames = this.fileService.storeMultipleFiles(files, "product");
            List<ProductImages> imagesDB = product.getImages();
            // imagesDB.clear();
            for (String fileName1 : fileNames) {
                ProductImages productImages = this.productImagesService.saveImageProduct(fileName1, product);
                imagesDB.add(productImages);
            }
            product.setImages(imagesDB);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) throws IdInvalidException {
        this.productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Product> postMethodName(@PathVariable("id") long id) throws IdInvalidException {
        Product entity = this.productService.rollbackDelete(id);
        return ResponseEntity.ok(entity);
    }

}