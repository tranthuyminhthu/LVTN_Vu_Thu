package org.example.orderservice.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String status; // PENDING, PAID, SHIPPED, CANCELLED

    private Double totalAmount;

    private String paymentStatus; // PENDING, SUCCESS, FAILED
    private String paymentMethod;
    private String paymentTransactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    // Getters, setters, constructors
    public OrderEntity() {}

    // ... getters and setters ...
} 