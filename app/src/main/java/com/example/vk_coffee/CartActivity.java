package com.example.vk_coffee;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.Api.CreateOrder;
import com.example.vk_coffee.adapter.CartAdapter;
import com.example.vk_coffee.model.Coffee;
import com.example.vk_coffee.model.Order;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.Executors;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView txtTotalPrice;
    private Button btnPayment;
    private List<Coffee> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Allow network on main thread (only for ZaloPay)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnPayment = findViewById(R.id.btnCheckout);

        cart = com.example.vk_coffee.CartSingleton.getInstance().getCart();

        updateTotalPrice();

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        CartAdapter cartAdapter = new CartAdapter(cart, this::removeItemFromCart);
        recyclerViewCart.setAdapter(cartAdapter);

        btnPayment.setOnClickListener(v -> {
            // Handle thanh toán here
            String totalPriceText = txtTotalPrice.getText().toString();
            String totalPrice = totalPriceText.replaceAll("[^\\d.]", "");

            if (totalPrice.isEmpty()) {
                Toast.makeText(CartActivity.this, "Bạn không có gì để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateOrder orderApi = new CreateOrder();
            try {
                JSONObject data = orderApi.createOrder(totalPrice);
                String code = data.getString("return_code");

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    ZaloPaySDK.getInstance().payOrder(CartActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String s, String s1, String s2) {
                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
                            intent.putExtra("paymentResult", "oke");
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            Toast.makeText(CartActivity.this, "Hủy thanh toán", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Toast.makeText(CartActivity.this, "Thanh toán lỗi: " + zaloPayError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
