package com.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.entity.Inventory;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
public class AdminInventoryController {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse> getInventory() {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Inventory fetched successfully").data(inventoryRepository.findAll()).build());
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<ApiResponse> getLowStock() {
        List<Inventory> lowStock = inventoryRepository.findByAvailableStockLessThanEqual(10);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Low stock products fetched").data(lowStock).build());
    }
}
