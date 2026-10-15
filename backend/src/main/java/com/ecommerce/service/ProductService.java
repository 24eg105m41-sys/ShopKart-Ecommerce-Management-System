package com.ecommerce.service;
        
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Inventory;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public Page<ProductDto> getProducts(String search, Long categoryId, String stockFilter, Pageable pageable) {
        return productRepository.searchProducts(search, categoryId, stockFilter, pageable).map(ProductDto::fromEntity);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductDto.fromEntity(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku().trim())) {
            throw new DuplicateResourceException("Product SKU already exists");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Product product = Product.builder()
            .name(dto.getName().trim())
            .description(dto.getDescription())
            .sku(dto.getSku().trim())
            .price(dto.getPrice())
            .discount(dto.getDiscount() == null ? BigDecimal.ZERO : dto.getDiscount())
            .imageUrl(dto.getImageUrl())
            .category(category)
            .stock(dto.getStock() == null ? 0 : dto.getStock())
            .status(dto.getStatus() == null ? "ACTIVE" : dto.getStatus())
            .build();

        Product saved = productRepository.save(product);

        Inventory inventory = Inventory.builder()
            .product(saved)
            .currentStock(saved.getStock())
            .reservedStock(0)
            .availableStock(saved.getStock())
            .lowStockThreshold(10)
            .build();
        inventoryRepository.save(inventory);

        return ProductDto.fromEntity(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (dto.getSku() != null && !dto.getSku().trim().equals(product.getSku()) && productRepository.existsBySku(dto.getSku().trim())) {
            throw new DuplicateResourceException("Product SKU already exists");
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        if (dto.getName() != null) product.setName(dto.getName().trim());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getSku() != null) product.setSku(dto.getSku().trim());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getDiscount() != null) product.setDiscount(dto.getDiscount());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getStatus() != null) product.setStatus(dto.getStatus());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        Inventory inventory = inventoryRepository.findByProduct(saved).orElseGet(() -> Inventory.builder().product(saved).build());
        inventory.setCurrentStock(saved.getStock());
        inventory.setAvailableStock(saved.getStock() - inventory.getReservedStock());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return ProductDto.fromEntity(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        inventoryRepository.findByProduct(product).ifPresent(inventoryRepository::delete);
        productRepository.delete(product);
    }

    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream().map(ProductDto::fromEntity).toList();
    }
}
