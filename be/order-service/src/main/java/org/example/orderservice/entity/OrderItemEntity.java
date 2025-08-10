package org.example.orderservice.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;

    // Getters, setters, constructors
    public OrderItemEntity() {}

    // ... getters and setters ...
} 