package com.project.LaptopShop.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.LaptopShop.domain.Product;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.repository.ProductRepository;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ProductService(ProductRepository productRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.productRepository = productRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public ResultPaginationDTO fetchProduct(Pageable pageable, Specification<Product> spec) {
        FilterNode node = filterParser.parse("deleted='" + false + "'");
        FilterSpecification<Product> spec1 = filterSpecificationConverter.convert(node);
        spec = spec.and(spec1);
        Page<Product> pageProduct = this.productRepository.findAll(spec, pageable);

        // Điều chỉnh số trang nếu vượt quá tổng số trang
        int totalPages = pageProduct.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber() && totalPages != 0) {
            pageable = pageable.withPage(totalPages - 1);
            pageProduct = this.productRepository.findAll(spec, pageable);
        }

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageProduct.getTotalPages());
        meta.setTotal(pageProduct.getTotalElements());
        res.setMeta(meta);

        res.setResult(pageProduct.getContent());
        return res;
    }

    public Product saveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Product getProductById(long id) throws IdInvalidException {
        return this.productRepository.findById(id).orElseThrow(() -> new IdInvalidException("ID product not found"));
    }

    public void deleteProduct(long id) throws IdInvalidException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("ID product not found"));
        product.setDeleted(true);
        product.setDeletedAt(Instant.now());
        this.productRepository.save(product);
    }

    public Product rollbackDelete(long id) throws IdInvalidException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("ID product not found"));
        product.setDeleted(false);
        product.setDeletedAt(null);
        return this.productRepository.save(product);
    }

    @Scheduled(fixedDelay = 20000)
    @Transactional
    public void hardDeleteExpiredUsers() {
        Instant cutoffTime = Instant.now().minusSeconds(20);
        List<Product> productDelete = this.productRepository.findByDeletedTrueAndDeletedAtBefore(cutoffTime);
        if (!productDelete.isEmpty()) {
            this.productRepository.deleteAll(productDelete);
        }
    }

    public Product handleBuyProduct(long id, long quantity) throws IdInvalidException {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("ID product not found"));
        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() < 0)
            throw new IdInvalidException("Quantity not enough");
        product.setSold(product.getSold() + quantity);
        return this.productRepository.save(product);
    }
}
