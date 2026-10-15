package com.ecommerce.repository;

import com.ecommerce.entity.Inventory;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);

    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findByAvailableStockLessThanEqual(int threshold);
}
