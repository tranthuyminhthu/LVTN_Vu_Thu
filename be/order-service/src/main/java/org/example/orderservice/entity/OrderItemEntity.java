package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private String productSku;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
    private String image;

} 
