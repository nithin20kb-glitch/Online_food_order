package com.foodexpress.service;

import com.foodexpress.entity.Restaurant;
import com.foodexpress.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getOpenRestaurants() {
        return restaurantRepository.findByOpenTrue();
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByLocation(String location) {
        if (location == null || location.isBlank() || "all".equalsIgnoreCase(location)) {
            return getOpenRestaurants();
        }
        return restaurantRepository.findByLocationAreaContainingIgnoreCaseAndOpenTrue(location.trim());
    }

    public Optional<Restaurant> getById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
}
