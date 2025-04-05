package com.project.LaptopShop.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.LaptopShop.domain.Factory;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.FactoryService;
import com.project.LaptopShop.service.FileService;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/admin/factory")
public class AFactoryController {
    private final FileService fileService;
    private final FactoryService factoryService;

    public AFactoryController(FileService fileService, FactoryService factoryService) {
        this.fileService = fileService;
        this.factoryService = factoryService;
    }

    @PostMapping
    public ResponseEntity<Factory> createFactory(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileLaptop") MultipartFile fileLaptop,
            @RequestParam("name") String name,
            @RequestParam("country") String country) throws URISyntaxException, IOException {
        // Lưu file
        Factory factory = new Factory();
        factory.setCountry(country);
        factory.setName(name);
        this.fileService.createUploadFolder("factory");
        String fileName = this.fileService.store(file, "factory");
        String fileNameL = this.fileService.store(fileLaptop, "factory");
        factory.setLaptopImage(fileNameL);
        factory.setImage(fileName);
        // Lưu factory
        return ResponseEntity.status(HttpStatus.CREATED).body(this.factoryService.saveFactory(factory));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getFactories(@Filter Specification<Factory> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.factoryService.fetchFactories(pageable, spec));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactory(@PathVariable("id") long id) throws IdInvalidException {
        this.factoryService.deleteFactory(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Factory> rollbackFactory(@PathVariable("id") long id) throws IdInvalidException {
        if (id == 0)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().body(this.factoryService.rollbackDelete(id));
    }
}
