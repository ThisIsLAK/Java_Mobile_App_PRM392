package com.example.vk_coffee.model;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "isReviewed")
    private boolean isReviewed;
    private String username;
    private String itemsJson;
    private int totalAmount;
    private long timestamp;

    public Order(String username, String itemsJson, int totalAmount, long timestamp) {
        this.username = username;
        this.itemsJson = itemsJson;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
        this.isReviewed = isReviewed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(String itemsJson) { this.itemsJson = itemsJson; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isReviewed() {
        return isReviewed;
    }
    public void setReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }
}
