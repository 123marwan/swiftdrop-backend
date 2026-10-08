package com.marawan.swiftdrop.repository;

import com.marawan.swiftdrop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // تأكد من استيراد هذه المكتبة

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // أضف هذا السطر لحل مشكلة findByEmail
    Optional<User> findByEmail(String email);

    User findByUsername(String username);
}