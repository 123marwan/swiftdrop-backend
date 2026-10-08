package com.marawan.swiftdrop.controller;

import com.marawan.swiftdrop.dto.OrderRequest;
import com.marawan.swiftdrop.entity.Order;
import com.marawan.swiftdrop.entity.User;
import com.marawan.swiftdrop.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderController(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request,
                                         @AuthenticationPrincipal User principal) {
        if (request.getOrderDetails() == null || request.getOrderDetails().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "orderDetails is required"));
        }

        Order order = new Order();
        order.setOrderDetails(request.getOrderDetails());
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCustomer(principal);
        order.setDriver(null);

        Order savedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/order.pending", savedOrder);

        return ResponseEntity.ok(Map.of(
                "message", "Order created successfully",
                "orderId", savedOrder.getId()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CUSTOMER','DRIVER')")
    public ResponseEntity<List<Order>> getAllOrders(@AuthenticationPrincipal User principal) {
        if ("DRIVER".equals(principal.getRole())) {
            return ResponseEntity.ok(orderRepository.findByDriver(principal));
        }
        return ResponseEntity.ok(orderRepository.findByCustomer(principal));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<Order>> getPendingOrders() {
        return ResponseEntity.ok(orderRepository.findByStatus(Order.OrderStatus.PENDING));
    }

    @PostMapping("/accept/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> acceptOrder(@PathVariable Long id,
                                         @AuthenticationPrincipal User principal) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
        }
        if (order.getStatus() != Order.OrderStatus.PENDING || order.getDriver() != null) {
            return ResponseEntity.status(409).body(Map.of("error", "Order is no longer available"));
        }

        order.setDriver(principal);
        order.setStatus(Order.OrderStatus.ON_THE_WAY);
        Order updatedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/order.status", updatedOrder);

        return ResponseEntity.ok(Map.of("message", "Order accepted successfully", "orderId", updatedOrder.getId()));
    }

    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestParam String status,
                                               @AuthenticationPrincipal User principal) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
        }
        if (order.getDriver() == null || !order.getDriver().getId().equals(principal.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only update orders assigned to you"));
        }

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value: " + status));
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/order.status", updatedOrder);

        return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
    }
}