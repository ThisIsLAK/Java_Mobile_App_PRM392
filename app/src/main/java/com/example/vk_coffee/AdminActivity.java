package com.example.vk_coffee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.adapter.CoffeeAdapterAdmin;
import com.example.vk_coffee.db.AppDatabase;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Coffee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCoffeeAdmin;
    private Button btnAddCoffee, btnViewDashboard; // ✅ thêm nút dashboard
    private CoffeeAdapterAdmin adapter;
    private List<Coffee> coffeeList;

    private ActivityResultLauncher<Intent> addCoffeeLauncher;
    private ActivityResultLauncher<Intent> editCoffeeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerViewCoffeeAdmin = findViewById(R.id.recyclerViewCoffeeAdmin);
        btnAddCoffee = findViewById(R.id.btnAddCoffee);
        btnViewDashboard = findViewById(R.id.btnViewDashboard); // ✅ ánh xạ nút

        coffeeList = new ArrayList<>();
        adapter = new CoffeeAdapterAdmin(coffeeList, this::deleteCoffee, this::editCoffee);
        recyclerViewCoffeeAdmin.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCoffeeAdmin.setAdapter(adapter);

        getCoffeeListFromDatabase();

        addCoffeeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        getCoffeeListFromDatabase();
                    }
                });

        editCoffeeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        getCoffeeListFromDatabase();
                    }
                });

        btnAddCoffee.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddEditCoffeeActivity.class);
            addCoffeeLauncher.launch(intent);
        });

        // ✅ Thêm sự kiện mở Dashboard
        btnViewDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    private void editCoffee(Coffee coffee) {
        Intent intent = new Intent(AdminActivity.this, AddEditCoffeeActivity.class);
        intent.putExtra("coffeeId", coffee.getId());
        editCoffeeLauncher.launch(intent);
    }

    private void getCoffeeListFromDatabase() {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Coffee> coffees = db.coffeeDao().getAllCoffees();
            runOnUiThread(() -> {
                coffeeList.clear();
                coffeeList.addAll(coffees);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void deleteCoffee(Coffee coffee) {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        new Thread(() -> {
            db.coffeeDao().delete(coffee);
            runOnUiThread(() -> {
                coffeeList.remove(coffee);
                adapter.notifyDataSetChanged();
                Toast.makeText(AdminActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
