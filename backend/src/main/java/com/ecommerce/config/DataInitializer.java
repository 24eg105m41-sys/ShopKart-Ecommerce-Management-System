package com.ecommerce.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecommerce.entity.Address;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Inventory;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedCategories();
        seedUsers();
        seedProducts();
        seedOrders();
    }

    private void seedRoles() {
        List.of("ADMIN", "HR", "CUSTOMER", "MERCHANT").forEach(name ->
            roleRepository.findByName(name).orElseGet(() -> roleRepository.save(Role.builder().name(name).build()))
        );
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                Category.builder().name("Electronics").description("Smart gadgets and accessories").status("ACTIVE").build(),
                Category.builder().name("Fashion").description("Apparel and fashion essentials").status("ACTIVE").build(),
                Category.builder().name("Home & Living").description("Home essentials and decor").status("ACTIVE").build(),
                Category.builder().name("Groceries").description("Daily grocery essentials").status("ACTIVE").build(),
                Category.builder().name("Beauty").description("Beauty and personal care").status("ACTIVE").build()
            ));
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role hrRole = roleRepository.findByName("HR").orElseThrow();
            Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

            userRepository.saveAll(List.of(
                User.builder().name("Admin User").email("admin@shopkart.com").password(passwordEncoder.encode("Admin@123")).phone("9876543210").role(adminRole).enabled(true).build(),
                User.builder().name("HR Manager").email("hr@shopkart.com").password(passwordEncoder.encode("Hr@123")).phone("9876543211").role(hrRole).enabled(true).build(),
                User.builder().name("Rahul Sharma").email("rahul@example.com").password(passwordEncoder.encode("Customer@123")).phone("9123456780").role(customerRole).enabled(true).build(),
                User.builder().name("Priya Verma").email("priya@example.com").password(passwordEncoder.encode("Customer@123")).phone("9123456781").role(customerRole).enabled(true).build(),
                User.builder().name("Amit Singh").email("amit@example.com").password(passwordEncoder.encode("Customer@123")).phone("9123456782").role(customerRole).enabled(true).build(),
                User.builder().name("Neha Gupta").email("neha@example.com").password(passwordEncoder.encode("Customer@123")).phone("9123456783").role(customerRole).enabled(true).build()
            ));
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            Category electronics = categoryRepository.findByName("Electronics").orElseThrow();
            Category fashion = categoryRepository.findByName("Fashion").orElseThrow();
            Category home = categoryRepository.findByName("Home & Living").orElseThrow();
            Category groceries = categoryRepository.findByName("Groceries").orElseThrow();
            Category beauty = categoryRepository.findByName("Beauty").orElseThrow();

            List<Product> products = List.of(
                Product.builder().name("Noise Smart Watch").description("Bluetooth calling smartwatch").sku("NW-001").price(new BigDecimal("2999")).discount(new BigDecimal("300")).imageUrl("https://images.unsplash.com/photo/1546868871-7041f2a55e12?auto=format&fit=crop&w=600&q=80").category(electronics).stock(14).status("ACTIVE").build(),
                Product.builder().name("Acer Laptop").description("Thin and light business laptop").sku("ACR-204").price(new BigDecimal("54999")).discount(new BigDecimal("3500")).imageUrl("https://images.unsplash.com/photo/1496181133206-80ce9b88a853?auto=format&fit=crop&w=600&q=80").category(electronics).stock(7).status("ACTIVE").build(),
                Product.builder().name("Cotton T-Shirt").description("Premium cotton regular fit").sku("FAS-101").price(new BigDecimal("899")).discount(new BigDecimal("100")).imageUrl("https://images.unsplash.com/photo/1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=80").category(fashion).stock(23).status("ACTIVE").build(),
                Product.builder().name("Kitchen Mixer").description("High-power mixer grinder").sku("HOM-543").price(new BigDecimal("3899")).discount(new BigDecimal("400")).imageUrl("https://images.unsplash.com/photo/1583394838336-acd977736f90?auto=format&fit=crop&w=600&q=80").category(home).stock(5).status("ACTIVE").build(),
                Product.builder().name("Rice 5kg").description("Premium basmati rice").sku("GRO-221").price(new BigDecimal("499")).discount(new BigDecimal("40")).imageUrl("https://images.unsplash.com/photo-1518843875459-f738682238a6?auto=format&fit=crop&w=600&q=80").category(groceries).stock(18).status("ACTIVE").build(),
                Product.builder().name("Face Serum").description("Hydrating vitamin c serum").sku("BEA-678").price(new BigDecimal("1499")).discount(new BigDecimal("200")).imageUrl("https://images.unsplash.com/photo-1571781926291-c477ebfd024b?auto=format&fit=crop&w=600&q=80").category(beauty).stock(12).status("ACTIVE").build(),
                Product.builder().name("Bluetooth Speaker").description("Portable wireless speaker").sku("BLU-445").price(new BigDecimal("2299")).discount(new BigDecimal("300")).imageUrl("https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=600&q=80").category(electronics).stock(9).status("ACTIVE").build(),
                Product.builder().name("Desk Lamp").description("Modern LED desk lamp").sku("LMP-908").price(new BigDecimal("1499")).discount(new BigDecimal("150")).imageUrl("https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=600&q=80").category(home).stock(6).status("ACTIVE").build()
            );

            List<Product> savedProducts = productRepository.saveAll(products);
            for (Product product : savedProducts) {
                Inventory inventory = Inventory.builder()
                    .product(product)
                    .currentStock(product.getStock())
                    .reservedStock(0)
                    .availableStock(product.getStock())
                    .lowStockThreshold(10)
                    .updatedAt(LocalDateTime.now())
                    .build();
                inventoryRepository.save(inventory);
            }
        }
    }

    private void seedOrders() {
        if (orderRepository.count() == 0) {
            User customer = userRepository.findByEmail("rahul@example.com").orElseThrow();
            User anotherCustomer = userRepository.findByEmail("priya@example.com").orElseThrow();
            Product product1 = productRepository.findAll().get(0);
            Product product2 = productRepository.findAll().get(1);

            Address address1 = addressRepository.save(Address.builder().user(customer).street("12 MG Road").city("Bengaluru").state("Karnataka").postalCode("560001").country("India").defaultAddress(true).build());
            Address address2 = addressRepository.save(Address.builder().user(anotherCustomer).street("9 Sector 15").city("Delhi").state("Delhi").postalCode("110020").country("India").defaultAddress(true).build());

            Order order1 = orderRepository.save(Order.builder()
                .orderNumber("ORD-1001")
                .customer(customer)
                .subtotal(new BigDecimal("3998.00"))
                .discount(new BigDecimal("300.00"))
                .tax(new BigDecimal("420.00"))
                .shippingCharge(new BigDecimal("80.00"))
                .totalAmount(new BigDecimal("4198.00"))
                .status("DELIVERED")
                .paymentStatus("PAID")
                .shippingAddress(address1)
                .build());

            OrderItem item1 = OrderItem.builder().order(order1).product(product1).quantity(1).unitPrice(product1.getPrice()).subtotal(product1.getFinalPrice()).build();
            OrderItem item2 = OrderItem.builder().order(order1).product(product2).quantity(1).unitPrice(product2.getPrice()).subtotal(product2.getFinalPrice()).build();
            order1.setItems(List.of(item1, item2));
            orderRepository.save(order1);

            Order order2 = orderRepository.save(Order.builder()
                .orderNumber("ORD-1002")
                .customer(anotherCustomer)
                .subtotal(new BigDecimal("2498.00"))
                .discount(BigDecimal.ZERO)
                .tax(new BigDecimal("290.00"))
                .shippingCharge(new BigDecimal("50.00"))
                .totalAmount(new BigDecimal("2838.00"))
                .status("PROCESSING")
                .paymentStatus("PAID")
                .shippingAddress(address2)
                .build());

            OrderItem item3 = OrderItem.builder().order(order2).product(product2).quantity(1).unitPrice(product2.getPrice()).subtotal(product2.getFinalPrice()).build();
            order2.setItems(List.of(item3));
            orderRepository.save(order2);
        }
    }
}
