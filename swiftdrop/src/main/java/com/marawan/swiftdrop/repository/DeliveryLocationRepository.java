package com.marawan.swiftdrop.repository;

import com.marawan.swiftdrop.entity.DeliveryLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation, Long> {
}