package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) AND (:categoryId IS NULL OR p.category.id = :categoryId) AND (:stockFilter IS NULL OR (:stockFilter = 'LOW' AND p.stock <= 10) OR (:stockFilter = 'AVAILABLE' AND p.stock > 10)) ORDER BY p.id DESC")
    Page<Product> searchProducts(@Param("search") String search, @Param("categoryId") Long categoryId, @Param("stockFilter") String stockFilter, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.stock <= 10 ORDER BY p.stock ASC")
    List<Product> findLowStockProducts();

    boolean existsBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);
}
