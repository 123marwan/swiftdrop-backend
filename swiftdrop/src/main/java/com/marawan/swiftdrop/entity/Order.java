package com.marawan.swiftdrop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderDetails;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, PREPARING, ON_THE_WAY, DELIVERED

    // ربط الطلب بالعميل اللي طلبه
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    // ربط الطلب بالسائق اللي هيوصله
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    public enum OrderStatus {
        PENDING, PREPARING, ON_THE_WAY, DELIVERED
    }
}