package com.ecommerce.service;

import com.ecommerce.dto.DashboardSummaryDto;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DashboardSummaryDto getSummary() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.countAllOrders();
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        long pendingOrders = orderRepository.countByStatus("PENDING");
        long deliveredOrders = orderRepository.countByStatus("DELIVERED");
        long cancelledOrders = orderRepository.countByStatus("CANCELLED");
        long lowStockProducts = productRepository.findLowStockProducts().size();

        return DashboardSummaryDto.builder()
            .totalUsers(totalUsers)
            .totalCustomers(userRepository.count())
            .totalProducts(totalProducts)
            .totalOrders(totalOrders)
            .totalRevenue(totalRevenue)
            .pendingOrders(pendingOrders)
            .deliveredOrders(deliveredOrders)
            .cancelledOrders(cancelledOrders)
            .lowStockProducts(lowStockProducts)
            .build();
    }

    public List<Map<String, Object>> getSalesOverview() {
        return orderRepository.getMonthlySales().stream().map(row -> Map.of(
            "month", row[0] + "/" + row[1],
            "sales", row[2],
            "orders", row[3]
        )).toList();
    }
}
