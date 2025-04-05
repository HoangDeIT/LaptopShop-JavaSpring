package com.project.LaptopShop.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.LaptopShop.domain.Factory;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.service.FactoryService;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1/factory")
public class FactoryController {
    private final FactoryService factoryService;

    public FactoryController(FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factory> getFactory(@PathVariable Long id) {
        Factory factory = factoryService.findById(id);
        if (factory != null) {
            return ResponseEntity.ok(factory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllFactories(@Filter Specification<Factory> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.factoryService.fetchFactories(pageable, spec));
    }
}
