package com.marawan.swiftdrop.controller;

import com.marawan.swiftdrop.entity.User;
import com.marawan.swiftdrop.repository.UserRepository;
import com.marawan.swiftdrop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()) != null) {
                return ResponseEntity.status(400).body(Map.of("error", "Username already exists"));
            }
            if (user.getRole() == null) {
                user.setRole("CUSTOMER");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully in database"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null && checkPassword(password, existingUser)) {
            String token = jwtUtil.generateToken(existingUser.getUsername(), existingUser.getRole());
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "role", existingUser.getRole()));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

    @PostMapping("/register-driver")
    public ResponseEntity<?> registerDriver(@RequestBody User user) {
        try {
            user.setRole("DRIVER");

            // وضع كلمة مرور افتراضية لو لم يتم إرسالها
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword("12345678");
            }

            // وضع إيميل افتراضي لمنع خطأ الـ Not Null في قاعدة البيانات
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                String phonePart = (user.getPhone() != null && !user.getPhone().isEmpty()) ? user.getPhone() : String.valueOf(System.currentTimeMillis());
                user.setEmail(phonePart + "@swiftdrop.com");
            }

            // وضع اسم مستخدم افتراضي لو لم يوجد (يتم الاعتماد على رقم الهاتف)
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(user.getPhone() != null ? user.getPhone() : "driver_" + System.currentTimeMillis());
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Driver registered successfully in database"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private boolean checkPassword(String rawPassword, User user) {
        String stored = user.getPassword();

        // hash مطابق لـ BCrypt
        if (stored.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, stored);
        }

        // مستخدم قديم بكلمة مرور نصية صريحة -> تسجيل دخول + ترقية تلقائية للتشفير
        if (stored.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }
}