package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrderCreateRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.entity.OrderEntity;
import org.example.orderservice.entity.OrderItemEntity;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        OrderEntity order = new OrderEntity();
        order.setUserId(request.getUserId());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItemEntity> items = request.getItems().stream().map(itemDto -> {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProductId(itemDto.getProductId());
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setTotalPrice(itemDto.getPrice() * itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
        order.setTotalAmount(items.stream().mapToDouble(OrderItemEntity::getTotalPrice).sum());

        OrderEntity savedOrder = orderRepository.save(order);

        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(savedOrder.getId());
        response.setUserId(savedOrder.getUserId());
        response.setStatus(savedOrder.getStatus());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setPaymentStatus(savedOrder.getPaymentStatus());
        response.setPaymentMethod(savedOrder.getPaymentMethod());
        response.setPaymentTransactionId(savedOrder.getPaymentTransactionId());
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setItems(savedOrder.getItems().stream().map(item -> {
            OrderResponseDto.OrderItemDto dto = new OrderResponseDto.OrderItemDto();
            dto.setProductId(item.getProductId());
            dto.setProductName(item.getProductName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            return dto;
        }).collect(Collectors.toList()));
        return response;
    }
} 
