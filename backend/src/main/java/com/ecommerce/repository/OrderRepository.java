package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.customer c JOIN FETCH o.shippingAddress a LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product p WHERE (:search IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND (:status IS NULL OR o.status = :status) ORDER BY o.createdAt DESC")
    Page<Order> searchOrders(@Param("search") String search, @Param("status") String status, Pageable pageable);

    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT MONTH(o.createdAt), YEAR(o.createdAt), COALESCE(SUM(o.totalAmount), 0), COUNT(o.id) FROM Order o GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> getMonthlySales();
}
