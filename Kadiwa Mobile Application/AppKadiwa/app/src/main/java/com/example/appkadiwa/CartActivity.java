package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.util.CartAdapter;
import com.example.appkadiwa.util.CartItem;
import com.example.appkadiwa.util.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private ImageView mesbut, notbut;
    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private TextView emptyCartTextView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private CollectionReference cartItemsRef;
    private ListenerRegistration cartItemsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mesbut = findViewById(R.id.chat);
        notbut = findViewById(R.id.notif);

        mesbut = findViewById(R.id.chat);
        notbut = findViewById(R.id.notif);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cart);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    return true;
                case R.id.bottom_search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                    return true;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
                case R.id.bottom_cart:
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    finish();
                    return true;
            }
            return false;
        });

        notbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });

        mesbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, UserListActivity.class);
                startActivity(i);
            }
        });

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        emptyCartTextView = findViewById(R.id.emptyCartTextView);

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, totalPriceTextView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(layoutManager);
        cartRecyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartItemsRef = db.collection("users").document(uid).collection("cart");

        cartItemsListener = cartItemsRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error getting cart items", e);
                return;
            }

            cartItemList.clear();
            double totalPrice = 0;

            for (QueryDocumentSnapshot document : snapshot) {
                String name = document.getString("name");
                double price = document.getDouble("price");
                int quantity = document.getLong("quantity").intValue();
                String imageUrl = document.getString("imageUrl");

                CartItem cartItem = new CartItem(name, price, quantity, imageUrl);
                cartItemList.add(cartItem);

                // Calculate the total price
                totalPrice += price * quantity;
            }

            cartAdapter.notifyDataSetChanged();

            if (cartItemList.isEmpty()) {
                // If cartItemList is empty, display a message indicating an empty cart
                emptyCartTextView.setVisibility(View.VISIBLE);
                totalPriceTextView.setVisibility(View.GONE);
            } else {
                // If the cart items collection is not empty, display the total price
                emptyCartTextView.setVisibility(View.GONE);
                totalPriceTextView.setVisibility(View.VISIBLE);

                // Set the total price text
                totalPriceTextView.setText(String.format("Total Price: ₱%.2f", totalPrice));
            }
        });
    }

    private void checkout() {
        if (cartItemList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an order with the cart items and total price
        List<String> itemNames = new ArrayList<>();
        List<Double> itemPrices = new ArrayList<>();
        List<Integer> itemQuantities = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            itemNames.add(cartItem.getProductName());
            itemPrices.add(cartItem.getProductPrice());
            itemQuantities.add(cartItem.getProductQuantity());
        }

        Order order = new Order(cartItemList, calculateTotalPrice(cartItemList));
        order.setTimestamp(new Date().getTime());
        order.setOrderId(UUID.randomUUID().toString());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Save the order in the database
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("orders")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Order created successfully
                        String orderId = documentReference.getId();

                        // Perform any additional operations you need, such as processing payment

                        // Clear the cart after successful checkout
                        clearCart();

                        // Show a success message
                        Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to order confirmation activity
                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }
                });
    }

    private void clearCart() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference cartRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("cart");

        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Delete all documents in the Cart collection
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    // Handle the error
                    Toast.makeText(CartActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItemsListener != null) {
            cartItemsListener.remove();
        }
    }

    private double calculateTotalPrice(List<CartItem> cartItems) {
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getProductPrice() * cartItem.getProductQuantity();
        }
        return totalPrice;
    }
}
