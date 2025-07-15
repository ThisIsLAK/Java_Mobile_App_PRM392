package com.example.vk_coffee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.adapter.CoffeeAdapter;
import com.example.vk_coffee.db.AppDatabase;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Coffee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCoffee;
    private Button btnViewCart, btnShowMap;
    //    private List<Coffee> coffeeList = new ArrayList<>();
    private List<Coffee> cart = new ArrayList<>();
    private CoffeeAdapter coffeeAdapter;

    private ActivityResultLauncher<Intent> cartLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewCoffee = findViewById(R.id.recyclerViewCoffee);
        btnViewCart = findViewById(R.id.btnViewCart);
        btnShowMap = findViewById(R.id.btnShowMap);

//        ArrayList<Coffee> coffeeList = new ArrayList<>();
//        coffeeList.add(new Coffee("Cà phê sữa", 30000, R.drawable.coffee_sua));
//        coffeeList.add(new Coffee("Cà phê đen", 25000, R.drawable.coffee_den));
//        coffeeList.add(new Coffee("Espresso", 30000, R.drawable.espresso));
//        coffeeList.add(new Coffee("Cappuccino", 30000, R.drawable.cappuccino));
//        coffeeList.add(new Coffee("Americano", 30000, R.drawable.americano));

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
                Toast.makeText(MainActivity.this, "Da them mon", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewCoffee.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCoffee.setAdapter(coffeeAdapter);

        getCoffeeListFromDatabase();

        btnViewCart.setOnClickListener(v -> {
            com.example.vk_coffee.CartSingleton.getInstance().setCart(cart);

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            cartLauncher.launch(intent);
        });

        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnOrderHistory = findViewById(R.id.btnOrderHistory);
        btnOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });
    }


    private void getCoffeeListFromDatabase() {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Coffee> coffees = db.coffeeDao().getAllCoffees();

            // runOnUiThread dùng để đảm bảo rằng cập nhật giao diện (RecyclerView) sẽ diễn ra trên luồng chính.
            runOnUiThread(() -> {
                coffeeAdapter.updateCoffeeList(coffees);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && "oke".equals(intent.getStringExtra("paymentResult"))) {
            Toast.makeText(this, "Thanh toan thanh cong", Toast.LENGTH_SHORT).show();
            intent.removeExtra("paymentResult");
        }
    }



}

