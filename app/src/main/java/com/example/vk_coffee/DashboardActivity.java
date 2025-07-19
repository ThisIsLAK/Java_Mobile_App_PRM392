package com.example.vk_coffee;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vk_coffee.db.AppDatabase;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Order;
import com.example.vk_coffee.model.Review;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private TextView tvTotalCups;
    private TextView tvReviewStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        tvTotalCups = findViewById(R.id.tvTotalCups);
        tvReviewStats = findViewById(R.id.tvReviewStats);


        loadDashboardData();
        loadReviewStatistics();

    }

    private void loadDashboardData() {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Order> orders = db.orderDao().getAllOrders();

            // ✅ Tính tổng số ly theo từng loại nước
            Map<String, Integer> coffeeSales = new HashMap<>();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Item>>() {}.getType();

            for (Order order : orders) {
                List<Item> items = gson.fromJson(order.getItemsJson(), listType);
                for (Item item : items) {
                    int current = coffeeSales.getOrDefault(item.coffeeName, 0);
                    coffeeSales.put(item.coffeeName, current + item.quantity);
                }
            }

            runOnUiThread(() -> {
                if (coffeeSales.isEmpty()) {
                    tvTotalCups.setText("Chưa có dữ liệu bán hàng!");
                    return;
                }

                List<PieEntry> pieEntries = new ArrayList<>();
                List<BarEntry> barEntries = new ArrayList<>();
                int totalCups = 0;
                int index = 0;

                for (Map.Entry<String, Integer> entry : coffeeSales.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                    barEntries.add(new BarEntry(index++, entry.getValue()));
                    totalCups += entry.getValue();
                }

                // ✅ Pie Chart
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Top Nước Bán Chạy");
                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                pieChart.setData(new PieData(pieDataSet));
                pieChart.animateY(1000);
                pieChart.invalidate();

                // ✅ Bar Chart
                BarDataSet barDataSet = new BarDataSet(barEntries, "Số ly bán được");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barChart.setData(new BarData(barDataSet));
                barChart.animateY(1000);
                barChart.invalidate();

                // ✅ Tổng số ly
                tvTotalCups.setText("Tổng số ly: " + totalCups);
            });
        });
    }

    private void loadReviewStatistics() {
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Review> reviews = db.reviewDao().getAllReviews();
            float avgRating = 0;
            if (!reviews.isEmpty()) {
                float total = 0;
                for (Review r : reviews) total += r.getRating();
                avgRating = total / reviews.size();
            }
            String stats = "Tổng số đánh giá: " + reviews.size() + "\n"
                    + "Điểm trung bình: " + String.format("%.1f", avgRating);

            runOnUiThread(() -> tvReviewStats.setText(stats));
        });
    }

    // ✅ Lớp Item để parse JSON từ itemsJson
    private static class Item {
        String coffeeName;
        int quantity;
    }
}
