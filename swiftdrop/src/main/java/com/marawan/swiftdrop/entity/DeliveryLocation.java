package com.marawan.swiftdrop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;  // خط العرض
    private double longitude; // خط الطول

    // ربط الموقع بالسائق المخصص للطلب
    @OneToOne
    @JoinColumn(name = "driver_id")
    private User driver;
}