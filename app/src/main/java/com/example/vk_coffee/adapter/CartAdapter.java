package com.example.vk_coffee.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.R;
import com.example.vk_coffee.model.Coffee;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Coffee> cartList;
    private OnRemoveFromCartListener onRemoveFromCartListener;

    public interface OnRemoveFromCartListener {
        void onRemoveFromCart(Coffee coffee);
    }

    public CartAdapter(List<Coffee> cartList, OnRemoveFromCartListener listener) {
        this.cartList = cartList;
        this.onRemoveFromCartListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Coffee coffee = cartList.get(position);
        holder.txtCoffeeName.setText(coffee.getName());
        holder.txtCoffeePrice.setText(coffee.getPrice() + " VND");
        holder.txtQuantity.setText("x " + coffee.getQuantity());

        byte[] imageBytes = coffee.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imgCoffee.setImageBitmap(bitmap);
        } else {
            holder.imgCoffee.setImageResource(R.drawable.espresso); // Sử dụng ảnh mặc định nếu không có ảnh
        }

        holder.btnRemoveFromCart.setOnClickListener(v -> {
            if (onRemoveFromCartListener != null) {
                onRemoveFromCartListener.onRemoveFromCart(coffee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView txtCoffeeName, txtCoffeePrice, txtQuantity;
        Button btnRemoveFromCart;
        ImageView imgCoffee;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCoffeeName = itemView.findViewById(R.id.txtCoffeeName);
            txtCoffeePrice = itemView.findViewById(R.id.txtCoffeePrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnRemoveFromCart = itemView.findViewById(R.id.btnRemoveFromCart);
            imgCoffee = itemView.findViewById(R.id.imgCoffee); // Ánh xạ ImageView
        }

    }
}

