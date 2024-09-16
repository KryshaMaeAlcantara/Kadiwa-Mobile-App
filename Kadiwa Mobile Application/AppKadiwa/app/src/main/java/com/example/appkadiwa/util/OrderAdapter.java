package com.example.appkadiwa.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.util.Order;
import com.example.appkadiwa.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_notif, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.textOrderId.setText("Order ID: " + order.getOrderId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date(order.getTimestamp()));
        holder.textTimestamp.setText("Timestamp: " + timestamp);

        holder.textTotalPrice.setText("Total Price: ₱" + order.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId;
        TextView textTimestamp;
        TextView textTotalPrice;

        OrderViewHolder(View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.tvOrderId);
            textTimestamp = itemView.findViewById(R.id.tvTimestamp);
            textTotalPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
