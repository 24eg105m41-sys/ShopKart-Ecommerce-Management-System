package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shippingCharge;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private Long shippingAddressId;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    public static OrderDto fromEntity(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
            .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
            .subtotal(order.getSubtotal())
            .discount(order.getDiscount())
            .tax(order.getTax())
            .shippingCharge(order.getShippingCharge())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .paymentStatus(order.getPaymentStatus())
            .shippingAddressId(order.getShippingAddress() != null ? order.getShippingAddress().getId() : null)
            .shippingAddress(order.getShippingAddress() != null ? order.getShippingAddress().getStreet() + ", " + order.getShippingAddress().getCity() + ", " + order.getShippingAddress().getState() : null)
            .createdAt(order.getCreatedAt())
            .items(order.getItems() == null ? List.of() : order.getItems().stream().map(OrderItemDto::fromEntity).toList())
            .build();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public static OrderItemDto fromEntity(OrderItem item) {
        return OrderItemDto.builder()
            .id(item.getId())
            .productId(item.getProduct() != null ? item.getProduct().getId() : null)
            .productName(item.getProduct() != null ? item.getProduct().getName() : null)
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .subtotal(item.getSubtotal())
            .build();
    }
}
