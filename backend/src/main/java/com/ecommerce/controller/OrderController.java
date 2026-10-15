package com.ecommerce.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.OrderCreateRequest;
import com.ecommerce.dto.OrderStatusRequest;
import com.ecommerce.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','HR','MERCHANT')")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse> getOrders(@RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Orders fetched successfully")
            .data(orderService.getOrders(search, status, PageRequest.of(page, size))).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderCreateRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Order created successfully")
            .data(orderService.createOrder(authentication.getName(), request)).build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Order status updated successfully")
            .data(orderService.updateStatus(id, request.getStatus())).build());
    }
}