package com.example.appkadiwa.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.R;
import com.example.appkadiwa.util.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private TextView totalPriceTextView;


    public CartAdapter(List<CartItem> cartItemList, TextView totalPriceTextView) {
        this.cartItemList = cartItemList;
        this.totalPriceTextView = totalPriceTextView;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.productPriceTextView.setText(String.valueOf(cartItem.getProductPrice()));
        holder.productQuantityTextView.setText(String.valueOf(cartItem.getProductQuantity()));
        Picasso.get().load(cartItem.getImageUrl()).into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView productPriceTextView;
        private TextView productQuantityTextView;
        private ImageView productImageView;
        private ImageView removeItemImageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            removeItemImageView = itemView.findViewById(R.id.removeItemImageView);
        }
    }
}