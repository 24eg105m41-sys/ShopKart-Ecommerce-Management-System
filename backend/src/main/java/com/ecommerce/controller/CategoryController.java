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
import com.ecommerce.dto.CategoryDto;
import com.ecommerce.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
    public ResponseEntity<ApiResponse> getCategories(@RequestParam(required = false) String search,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDto> categories = categoryService.getCategories(search, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Categories fetched successfully").data(categories).build());
    }

    @GetMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Category fetched successfully").data(categoryService.getCategoryById(id)).build());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Category created successfully").data(categoryService.createCategory(dto)).build());
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Category updated successfully").data(categoryService.updateCategory(id, dto)).build());
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Category deleted successfully").build());
    }
}
