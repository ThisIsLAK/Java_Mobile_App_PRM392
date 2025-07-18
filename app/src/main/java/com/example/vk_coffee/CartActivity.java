package com.example.vk_coffee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.adapter.CartAdapter;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Coffee;
import com.example.vk_coffee.model.Order;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView txtTotalPrice;
    private Button btnCheckout;
    private List<Coffee> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        cart = com.example.vk_coffee.model.CartSingleton.getInstance().getCart();
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(new CartAdapter(cart, this::removeItemFromCart));

        updateTotalPrice();

        btnCheckout.setOnClickListener(v -> {
            String totalPriceText = txtTotalPrice.getText().toString();
            String numericPrice = totalPriceText.replaceAll("[^\\d]", ""); // chỉ lấy số

            if (numericPrice.isEmpty() || cart.isEmpty()) {
                Toast.makeText(CartActivity.this, "Bạn chưa có gì để đặt hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String username = prefs.getString("username", "guest");

            Gson gson = new Gson();
            String itemsJson = gson.toJson(cart);

            try {
                int totalPriceInt = Integer.parseInt(numericPrice);
                Order order = new Order(username, itemsJson, totalPriceInt, System.currentTimeMillis());
                Executors.newSingleThreadExecutor().execute(() -> {
                    DatabaseClient.getInstance(this).getAppDatabase().orderDao().insertOrder(order);
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Lỗi chuyển đổi tổng tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CartActivity.this, MainActivity.class));
            finish();
        });
    }

    private void removeItemFromCart(Coffee coffee) {
        int position = cart.indexOf(coffee);
        if (position != -1) {
            cart.remove(position);
            recyclerViewCart.getAdapter().notifyItemRemoved(position);
            updateTotalPrice();
        }
    }

    private void updateTotalPrice() {
        int totalPrice = 0;
        for (Coffee coffee : cart) {
            totalPrice += coffee.getPrice() * coffee.getQuantity();
        }
        txtTotalPrice.setText("Tổng tiền: " + totalPrice + " VND");
    }
}
