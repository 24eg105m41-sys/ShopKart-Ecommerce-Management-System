package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Page<CategoryDto> getCategories(String search, Pageable pageable) {
        return categoryRepository.searchCategories(search, pageable).map(CategoryDto::fromEntity);
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return CategoryDto.fromEntity(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.findByName(dto.getName().trim()).isPresent()) {
            throw new DuplicateResourceException("Category already exists");
        }

        Category category = Category.builder()
            .name(dto.getName().trim())
            .description(dto.getDescription())
            .status(dto.getStatus() == null ? "ACTIVE" : dto.getStatus())
            .build();

        return CategoryDto.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (dto.getName() != null && !dto.getName().trim().equals(category.getName()) && categoryRepository.findByName(dto.getName().trim()).isPresent()) {
            throw new DuplicateResourceException("Category already exists");
        }

        category.setName(dto.getName() == null ? category.getName() : dto.getName().trim());
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus() == null ? category.getStatus() : dto.getStatus());
        category.setUpdatedAt(LocalDateTime.now());
        return CategoryDto.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        List<Product> products = productRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category that contains products");
        }

        categoryRepository.delete(category);
    }
}
