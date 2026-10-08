package com.marawan.swiftdrop.controller;

import com.marawan.swiftdrop.entity.MenuItem;
import com.marawan.swiftdrop.entity.Restaurant;
import com.marawan.swiftdrop.repository.MenuItemRepository;
import com.marawan.swiftdrop.repository.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantController(RestaurantRepository restaurantRepository,
                                MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','DRIVER')")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','DRIVER')")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Restaurant not found"));
        }
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/{id}/menu")
    @PreAuthorize("hasAnyRole('CUSTOMER','DRIVER')")
    public ResponseEntity<?> getRestaurantMenu(@PathVariable Long id) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Restaurant not found"));
        }
        List<MenuItem> menu = menuItemRepository.findByRestaurantId(id);
        return ResponseEntity.ok(menu);
    }

    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> createRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getName() == null || restaurant.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Restaurant name is required"));
        }
        if (restaurant.getLatitude() < -90 || restaurant.getLatitude() > 90) {
            return ResponseEntity.badRequest().body(Map.of("error", "Latitude must be between -90 and 90"));
        }
        if (restaurant.getLongitude() < -180 || restaurant.getLongitude() > 180) {
            return ResponseEntity.badRequest().body(Map.of("error", "Longitude must be between -180 and 180"));
        }

        restaurant.setId(null);
        restaurant.setRating(0.0);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return ResponseEntity.status(201).body(savedRestaurant);
    }
}