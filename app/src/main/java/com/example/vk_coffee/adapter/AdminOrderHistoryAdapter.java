package com.example.vk_coffee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.R;
import com.example.vk_coffee.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderHistoryAdapter extends RecyclerView.Adapter<AdminOrderHistoryAdapter.AdminOrderViewHolder> {

    private List<Order> orderList = new ArrayList<>();

    public AdminOrderHistoryAdapter(List<Order> orders) {
        this.orderList = orders;
    }

    @NonNull
    @Override
    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout riêng cho admin
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order_history, parent, false);
        return new AdminOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        // Format timestamp
        String dateStr = "";
        long time = order.getTimestamp();
        if (time > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            dateStr = sdf.format(new Date(time));
        }

        // Bind dữ liệu
        holder.txtUser.setText("User: " + order.getUsername());
        holder.txtDateAdmin.setText("Ngày đặt: " + dateStr);
        holder.txtItemsAdmin.setText("Món đã chọn: " + order.getItemsJson());
        holder.txtTotalAdmin.setText("Tổng tiền: " + order.getTotalAmount() + " VND");
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        orderList.clear();
        orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtDateAdmin, txtItemsAdmin, txtTotalAdmin;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtDateAdmin = itemView.findViewById(R.id.txtDateAdmin);
            txtItemsAdmin = itemView.findViewById(R.id.txtItemsAdmin);
            txtTotalAdmin = itemView.findViewById(R.id.txtTotalAdmin);
        }
    }
}