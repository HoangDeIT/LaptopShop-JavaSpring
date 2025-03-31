package com.project.LaptopShop.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.LaptopShop.domain.Factory;
import com.project.LaptopShop.domain.Order;
import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.ResUserDTO;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.repository.OrderDetailRepository;
import com.project.LaptopShop.repository.OrderRepository;
import com.project.LaptopShop.util.SecurityUtil;
import com.project.LaptopShop.util.constant.StatusEnum;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.project.LaptopShop.util.error.IdInvalidException;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

@Service
public class OrderService {
    private final OrderRepository orderRepository;;
    private final OrderDetailRepository orderDetailRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            FilterParser filterParser, FilterSpecificationConverter filterSpecificationConverter,
            UserService userService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.userService = userService;
    }

    public Order createOrder(Order order) {

        Order entity = orderRepository.save(order);
        // this.orderDetailRepository.saveAll(order.getOrderDetails());
        return entity;
    }

    public void deleteOrder(long id) throws IdInvalidException {
        Order order = this.fetchOrderById(id);
        order.setDeleted(true);
        order.setDeletedAt(Instant.now());
        this.orderRepository.save(order);
    }

    @Transactional
    public Order rollbackOrder(long orderId) throws IdInvalidException {
        Order order = this.fetchOrderById(orderId);
        order.setDeleted(false);
        order.setDeletedAt(null);
        return this.orderRepository.save(order);
    }

    public ResultPaginationDTO fetchOrder(Pageable pageable, Specification<Order> spec) {
        String username = SecurityUtil.getCurrentUserLogin();
        TypeEnum type = SecurityUtil.getCurrentUserType();
        User user = this.userService.getUserByUserNameAndType(username, type);
        long idUser = user.getId();
        FilterNode node = filterParser.parse("deleted='" + false + "'");
        FilterNode node1 = filterParser.parse("user.id='" + idUser + "'");
        FilterSpecification<Order> spec1 = filterSpecificationConverter.convert(node);
        FilterSpecification<Order> spec2 = filterSpecificationConverter.convert(node1);
        spec = spec.and(spec1).and(spec2);
        Page<Order> pageOrder = this.orderRepository.findAll(spec, pageable);
        int totalPages = pageOrder.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber() && totalPages != 0) {
            pageable = pageable.withPage(totalPages - 1);
            pageOrder = this.orderRepository.findAll(spec, pageable);
        }

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());
        res.setMeta(meta);

        res.setResult(pageOrder.getContent());
        return res;
    }

    public ResultPaginationDTO fetchOrderAdmin(Pageable pageable, Specification<Order> spec) {
        Page<Order> pageOrder = this.orderRepository.findAll(spec, pageable);
        int totalPages = pageOrder.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber() && totalPages != 0) {
            pageable = pageable.withPage(totalPages - 1);
            pageOrder = this.orderRepository.findAll(spec, pageable);
        }

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());
        res.setMeta(meta);

        res.setResult(pageOrder.getContent());
        return res;
    }

    public Order fetchOrderById(long id) throws IdInvalidException {
        return this.orderRepository.findById(id).orElseThrow(() -> new IdInvalidException("Order not found"));
    }

    public long pendingCount() {
        return this.orderRepository.countByStatus(StatusEnum.PENDING);
    }
}
