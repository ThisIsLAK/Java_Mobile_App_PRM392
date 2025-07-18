package com.example.vk_coffee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.adapter.CoffeeAdapter;
import com.example.vk_coffee.db.AppDatabase;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Coffee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCoffee;
    private List<Coffee> cart = new ArrayList<>();
    private CoffeeAdapter coffeeAdapter;
    private ActivityResultLauncher<Intent> cartLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewCoffee = findViewById(R.id.recyclerViewCoffee);

        cartLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            List<Coffee> updatedCart = data.getParcelableArrayListExtra("cart");
                            if (updatedCart != null) {
                                cart.clear();
                                cart.addAll(updatedCart);
                            }
                        }
                    }
                }
        );

        coffeeAdapter = new CoffeeAdapter(new ArrayList<>(), coffee -> {
            if (coffee.getQuantity() > 0 && !cart.contains(coffee)) {
                cart.add(coffee);
                Toast.makeText(MainActivity.this, "Đã thêm món", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewCoffee.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCoffee.setAdapter(coffeeAdapter);

        getCoffeeListFromDatabase();

        // === Xử lý FAB Menu ===
        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_cart) {
                    com.example.vk_coffee.model.CartSingleton.getInstance().setCart(cart);
                    Intent cartIntent = new Intent(this, CartActivity.class);
                    cartLauncher.launch(cartIntent);
                    return true;
                } else if (id == R.id.menu_history) {
                    startActivity(new Intent(this, OrderHistoryActivity.class));
                    return true;
                } else if (id == R.id.menu_map) {
                    startActivity(new Intent(this, MapActivity.class));
                    return true;
                } else if (id == R.id.menu_logout) {
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }

    private void getCoffeeListFromDatabase() {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Coffee> coffees = db.coffeeDao().getAllCoffees();
            runOnUiThread(() -> coffeeAdapter.updateCoffeeList(coffees));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && "oke".equals(intent.getStringExtra("paymentResult"))) {
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            intent.removeExtra("paymentResult");
        }
    }
}