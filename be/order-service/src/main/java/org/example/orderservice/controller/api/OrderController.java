package org.example.orderservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.*;
import org.example.orderservice.service.OrderService;
import org.example.orderservice.service.VnPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final VnPayService vnPayService;

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

    @PostMapping("/payment")
    public ResponseEntity<?> createOrderWithPayment(@RequestBody VnpayReturnParams request) {
        try {
            // Chuyển đổi DTO sang Map để verify signature


            // Kiểm tra chữ ký số
            boolean valid = orderService.verifyPayment(request);
            if (!valid) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "success", false,
                        "message", "Invalid VNPay signature or Payment failed!"
                ));
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
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

    @GetMapping("/vendors")
    public ResponseEntity<OrderListResponseDto> getOrdersByVendor(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status){
        try {
            OrderListResponseDto response = orderService.getOrdersByVendorId(page, size, status);
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

    @GetMapping("/me")
    public ResponseEntity<OrderListResponseDto> getCurrentUserOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            OrderListResponseDto response = orderService.getMyOrder(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/delivering")
    public ResponseEntity<?> changeStatusToDelivering(@RequestBody OrderAcceptRequestDto order) {
        try {
            return ResponseEntity.ok().body(orderService.changeStatusToDelivering(order));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/accepted")
    public ResponseEntity<?> changeStatusToAccept(@RequestBody Map<String, String> order) {
        try {
            String orderId = order.get("orderId");
            orderService.changeStatusToAccept(orderId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/received")
    public ResponseEntity<?> changeStatusToReceive(@RequestBody Map<String, String> order) {
        try {
            String orderId = order.get("orderId");
            orderService.changeStatusToReceive(orderId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cancelled")
    public ResponseEntity<?> changeStatusToCancel(@RequestBody Map<String, String> order) {
        try {
            String orderId = order.get("orderId");
            String note = order.get("note");
            orderService.changeStatusToCancel(orderId, note);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

}
