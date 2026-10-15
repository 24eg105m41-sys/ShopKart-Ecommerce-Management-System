package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_product", columnList = "product_id", unique = true),
    @Index(name = "idx_inventory_low_stock", columnList = "available_stock")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer availableStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
