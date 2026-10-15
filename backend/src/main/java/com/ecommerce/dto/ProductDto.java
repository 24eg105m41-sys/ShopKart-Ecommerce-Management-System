package com.ecommerce.dto;

import com.ecommerce.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    private BigDecimal discount;
    private BigDecimal finalPrice;
    private String imageUrl;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .sku(product.getSku())
            .price(product.getPrice())
            .discount(product.getDiscount())
            .finalPrice(product.getFinalPrice())
            .imageUrl(product.getImageUrl())
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .stock(product.getStock())
            .status(product.getStatus())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
}
