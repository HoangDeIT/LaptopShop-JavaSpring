package com.project.LaptopShop.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.LaptopShop.domain.Order;
import com.project.LaptopShop.util.constant.StatusEnum;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    long countByStatus(StatusEnum status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.deleted = false")
    double getTotalRevenue();

    // Phiên b n c filter ng y
    @Query("""
            SELECT COALESCE(SUM(o.totalPrice), 0)
            FROM Order o
            WHERE o.deleted = false
                AND o.createdAt BETWEEN :startDate AND :endDate""")
    double getTotalRevenueBetweenDates(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    List<Order> findByDeletedTrueAndDeletedAtBefore(Instant cutoffTime);
}
