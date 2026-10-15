package com.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
    public ResponseEntity<ApiResponse> getProducts(@RequestParam(required = false) String search,
                                                 @RequestParam(required = false) Long categoryId,
                                                 @RequestParam(required = false) String stockFilter,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Page<ProductDto> products = productService.getProducts(search, categoryId, stockFilter, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Products fetched successfully").data(products).build());
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Product fetched successfully").data(productService.getProductById(id)).build());
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Product created successfully").data(productService.createProduct(dto)).build());
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Product updated successfully").data(productService.updateProduct(id, dto)).build());
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Product deleted successfully").build());
    }
}
