package org.example.orderservice.dao;

import org.example.orderservice.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDao extends JpaRepository<OrderItemEntity, String> {
} 
