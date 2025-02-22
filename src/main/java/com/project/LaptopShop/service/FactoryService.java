package com.project.LaptopShop.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.LaptopShop.domain.Factory;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.repository.FactoryRepository;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.transaction.Transactional;

@Service
public class FactoryService {
    private final FactoryRepository factoryRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public FactoryService(FactoryRepository factoryRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.factoryRepository = factoryRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public Factory findById(long id) {
        return factoryRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchFactories(Pageable pageable, Specification<Factory> spec) {
        FilterNode node = filterParser.parse("deleted='" + false + "'");
        FilterSpecification<Factory> spec1 = filterSpecificationConverter.convert(node);
        spec = spec.and(spec1);
        Page<Factory> pageFactory = this.factoryRepository.findAll(spec, pageable);

        // Điều chỉnh số trang nếu vượt quá tổng số trang
        int totalPages = pageFactory.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber()) {
            pageable = pageable.withPage(totalPages - 1);
            pageFactory = this.factoryRepository.findAll(spec, pageable);
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

    public Factory saveFactory(Factory factory) {
        return factoryRepository.save(factory);
    }

    public Factory deleteFactory(long id) throws IdInvalidException {
        Factory factory = factoryRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("ID factory not found"));
        factory.setDeleted(true);
        factory.setDeletedAt(Instant.now());
        return this.factoryRepository.save(factory);
    }

    @Transactional
    public Factory rollbackDelete(long factoryId) throws IdInvalidException {
        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new IdInvalidException("ID factory not found"));
        factory.setDeleted(false);
        factory.setDeletedAt(null);
        return this.factoryRepository.save(factory);
    }

    @Scheduled(fixedDelay = 20000)
    @Transactional
    public void hardDeleteExpiredFactories() {
        Instant cutoffTime = Instant.now().minusSeconds(20);
        List<Factory> factoriesToDelete = factoryRepository.findByDeletedTrueAndDeletedAtBefore(cutoffTime);
        if (!factoriesToDelete.isEmpty()) {
            factoryRepository.deleteAll(factoriesToDelete);
        }
    }

}
