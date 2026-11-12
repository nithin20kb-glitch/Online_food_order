package com.foodexpress.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "cuisine_type", nullable = false, length = 100)
    private String cuisineType;

    @Column(name = "location_area", nullable = false, length = 100)
    private String locationArea;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private Double rating = 4.5;

    @Column(name = "delivery_time_minutes", nullable = false)
    private Integer deliveryTimeMinutes = 30;

    @Column(name = "min_order_amount", nullable = false)
    private Double minOrderAmount = 100.0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_open", nullable = false)
    private boolean open = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodItem> foodItems = new ArrayList<>();

    public Restaurant() {
    }

    public Restaurant(String name, String cuisineType, String locationArea, String address,
                      Double rating, Integer deliveryTimeMinutes, Double minOrderAmount, String imageUrl) {
        this.name = name;
        this.cuisineType = cuisineType;
        this.locationArea = locationArea;
        this.address = address;
        this.rating = rating;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
        this.minOrderAmount = minOrderAmount;
        this.imageUrl = imageUrl;
        this.open = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getLocationArea() {
        return locationArea;
    }

    public void setLocationArea(String locationArea) {
        this.locationArea = locationArea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(Integer deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public Double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }
}
