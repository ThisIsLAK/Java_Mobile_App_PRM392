package com.example.vk_coffee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.dao.OrderDao;
import com.example.vk_coffee.db.DatabaseClient;
import com.example.vk_coffee.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", "");

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Order> orderList = DatabaseClient.getInstance(this)
                    .getAppDatabase().orderDao().getOrdersByUsername(username);

            runOnUiThread(() -> {
                if (orderList.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa đặt đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
                recyclerView.setAdapter(new OrderAdapter(orderList));
            });
        });
    }

    private static class OrderAdapter extends Adapter<OrderAdapter.OrderViewHolder> {
        private final List<Order> orders;

        OrderAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.txtOrderTotal.setText("Tổng tiền: " + order.getTotalAmount() + " VND");
            holder.txtOrderItems.setText("Món đã chọn: " + order.getItemsJson());
            holder.txtOrderDate.setText("Ngày đặt: " + formatDate(order.getTimestamp()));

            if (order.isReviewed()) {
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setClickable(false);
            } else {
                holder.tvStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ReviewActivity.class);
                intent.putExtra("orderId", order.getId());
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView txtOrderDate, txtOrderItems, txtOrderTotal, tvStatus;

            OrderViewHolder(View itemView) {
                super(itemView);
                txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
                txtOrderItems = itemView.findViewById(R.id.txtOrderItems);
                txtOrderTotal = itemView.findViewById(R.id.txtOrderTotal);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }

        private String formatDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}
