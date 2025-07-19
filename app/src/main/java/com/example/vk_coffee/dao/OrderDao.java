package com.example.vk_coffee.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vk_coffee.model.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insertOrder(Order order);

    @Query("SELECT * FROM `Order` ORDER BY timestamp DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM `Order` WHERE username = :username ORDER BY timestamp DESC")
    List<Order> getOrdersByUsername(String username);
    @Query("UPDATE `Order` SET isReviewed = 1 WHERE id = :orderId")
    void markOrderReviewed(int orderId);
}


