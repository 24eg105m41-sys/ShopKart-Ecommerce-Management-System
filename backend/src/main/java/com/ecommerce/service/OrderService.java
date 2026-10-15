package com.ecommerce.service;

import com.ecommerce.dto.OrderCreateRequest;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.entity.Address;
import com.ecommerce.entity.Inventory;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final InventoryRepository inventoryRepository;

    public Page<OrderDto> getOrders(String search, String status, Pageable pageable) {
        return orderRepository.searchOrders(search, status, pageable).map(OrderDto::fromEntity);
    }

    @Transactional
    public OrderDto createOrder(String email, OrderCreateRequest request) {
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = addressRepository.save(Address.builder()
            .user(customer)
            .street(request.getStreet().trim())
            .city(request.getCity().trim())
            .state(request.getState().trim())
            .postalCode(request.getPostalCode().trim())
            .country(request.getCountry().trim())
            .defaultAddress(false)
            .build());

        Order order = Order.builder()
            .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT))
            .customer(customer)
            .shippingAddress(address)
            .subtotal(BigDecimal.ZERO)
            .discount(BigDecimal.ZERO)
            .tax(BigDecimal.ZERO)
            .shippingCharge(BigDecimal.ZERO)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderCreateRequest.OrderLineRequest requestedItem : request.getItems()) {
            Product product = productRepository.findById(requestedItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + requestedItem.getProductId()));
            if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                throw new IllegalArgumentException("Product is not active: " + product.getName());
            }
            if (product.getStock() < requestedItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal lineTotal = product.getFinalPrice().multiply(BigDecimal.valueOf(requestedItem.getQuantity()));
            total = total.add(lineTotal);
            product.setStock(product.getStock() - requestedItem.getQuantity());
            product.setUpdatedAt(LocalDateTime.now());
            Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseGet(() -> Inventory.builder().product(product).reservedStock(0).lowStockThreshold(10).build());
            inventory.setCurrentStock(product.getStock());
            inventory.setAvailableStock(product.getStock() - inventory.getReservedStock());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            order.getItems().add(OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(requestedItem.getQuantity())
                .unitPrice(product.getFinalPrice())
                .subtotal(lineTotal)
                .build());
        }

        order.setSubtotal(total);
        order.setTotalAmount(total);
        return OrderDto.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderDto updateStatus(Long id, String status) {
        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        if (!java.util.Set.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED").contains(normalizedStatus)) {
            throw new IllegalArgumentException("Unsupported order status");
        }
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(normalizedStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderDto.fromEntity(orderRepository.save(order));
    }
}