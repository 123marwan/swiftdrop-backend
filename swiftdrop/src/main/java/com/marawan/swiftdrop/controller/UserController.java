package com.marawan.swiftdrop.controller;

import com.marawan.swiftdrop.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", principal.getId());
        profile.put("username", principal.getUsername());
        profile.put("email", principal.getEmail());
        profile.put("phone", principal.getPhone());
        profile.put("role", principal.getRole());
        return ResponseEntity.ok(profile);
    }
}