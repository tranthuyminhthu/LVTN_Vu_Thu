package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String vendorId;
    private String ghnId;
    private String userId;

    private String status;

    private Double totalAmount;

    private String paymentStatus;
    private String paymentMethod;
    private String paymentTransactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String receiverAddress;
    private String receiverWardCode;
    private Integer receiverDistrictId;
    private String note;
    private Double fee;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

} 
