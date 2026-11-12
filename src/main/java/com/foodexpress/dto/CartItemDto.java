package com.foodexpress.dto;

import java.io.Serializable;

public class CartItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long foodId;
    private String foodName;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private boolean vegetarian;
    private String restaurantName;
    private boolean available = true;

    public CartItemDto() {
    }

    public CartItemDto(Long foodId, String foodName, Double price, Integer quantity,
                       String imageUrl, boolean vegetarian, String restaurantName, boolean available) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.vegetarian = vegetarian;
        this.restaurantName = restaurantName;
        this.available = available;
    }

    public Double getSubtotal() {
        if (price == null || quantity == null) return 0.0;
        return price * quantity;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
