package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrderCreateRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.dto.OrderListResponseDto;
import org.example.orderservice.dto.OrderStatusUpdateRequestDto;
import org.example.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateRequestDto request) {
        try {
            OrderResponseDto response = orderService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error and return a meaningful response
            return ResponseEntity.status(500).body(null); // Replace with proper error handling
        }
    }

    @GetMapping("")
    public ResponseEntity<OrderListResponseDto> getOrders(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(required = false) String status) {
        try {
            OrderListResponseDto response = orderService.getOrders(page, size, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String orderId) {
        try {
            OrderResponseDto response = orderService.getOrderById(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequestDto request) {
        try {
            orderService.updateOrderStatus(orderId, request.getStatus(), request.getNote());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
} 
