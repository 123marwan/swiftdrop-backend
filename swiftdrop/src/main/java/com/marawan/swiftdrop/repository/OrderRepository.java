package com.marawan.swiftdrop.repository;

import com.marawan.swiftdrop.entity.Order;
import com.marawan.swiftdrop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByCustomer(User customer);

    List<Order> findByDriver(User driver);
}