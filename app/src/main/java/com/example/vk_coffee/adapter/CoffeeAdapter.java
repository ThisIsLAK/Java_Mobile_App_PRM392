package com.example.vk_coffee.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vk_coffee.R;
import com.example.vk_coffee.model.Coffee;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {

    private List<Coffee> coffeeList;
    private OnAddToCartListener onAddToCartListener;

    public CoffeeAdapter(List<Coffee> coffeeList, OnAddToCartListener onAddToCartListener) {
        this.coffeeList = coffeeList;
        this.onAddToCartListener = onAddToCartListener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        Coffee coffee = coffeeList.get(position);

        holder.txtCoffeeName.setText(coffee.getName());
        holder.txtCoffeePrice.setText(coffee.getPrice() + " VND");

        // Load dynamic image
        if (coffee.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(coffee.getImage(), 0, coffee.getImage().length);
            holder.imageCoffee.setImageBitmap(bitmap);
        } else {
            holder.imageCoffee.setImageResource(R.drawable.coffee_sua); // Optional placeholder
        }

        holder.numberPickerQuantity.setMinValue(0);
        holder.numberPickerQuantity.setMaxValue(5);
        holder.numberPickerQuantity.setOnValueChangedListener((picker, oldVal, newVal) -> coffee.setQuantity(newVal));

        holder.btnAddToCart.setOnClickListener(v -> {
            if (onAddToCartListener != null) {
                onAddToCartListener.onAddToCart(coffee);
            }
        });
    }



    @Override
    public int getItemCount() {
        return coffeeList.size();
    }


    public void updateCoffeeList(List<Coffee> newCoffeeList) {
        this.coffeeList.clear();
        this.coffeeList.addAll(newCoffeeList);
        notifyDataSetChanged();
    }

    public static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCoffee;
        TextView txtCoffeeName, txtCoffeePrice;
        NumberPicker numberPickerQuantity;
        Button btnAddToCart;

        public CoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoffee = itemView.findViewById(R.id.imgCoffee); // Make sure this matches your XML ID
            txtCoffeeName = itemView.findViewById(R.id.txtCoffeeName);
            txtCoffeePrice = itemView.findViewById(R.id.txtCoffeePrice);
            numberPickerQuantity = itemView.findViewById(R.id.numberPickerQuantity);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }


    public interface OnAddToCartListener {
        void onAddToCart(Coffee coffee);
    }
}


