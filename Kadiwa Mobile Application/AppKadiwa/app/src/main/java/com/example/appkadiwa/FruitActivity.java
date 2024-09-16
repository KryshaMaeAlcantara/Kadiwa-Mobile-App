package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.util.Product;
import com.example.appkadiwa.util.ProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FruitActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private static final String TAG = "FruitActivity";

    private ImageView backBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catfruit);

        backBut = findViewById(R.id.back);

        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FruitActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        // Initialize the RecyclerView, layout manager, and adapter
        recyclerView = findViewById(R.id.fruit_rec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);

        // Set the layout manager and adapter to the RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(productAdapter);

        // Set item click listener
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Create an intent to start ItemActivity
                Intent intent = new Intent(FruitActivity.this, ItemActivity.class);
                // Pass the necessary data as extras
                intent.putExtra("productName", product.getName());
                intent.putExtra("productImage", product.getImageUrl());
                intent.putExtra("productDescription", product.getDescription());
                intent.putExtra("productPrice", product.getPrice());
                // Start the ItemActivity
                startActivity(intent);
            }
        });

        // Query the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("item/fruits");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving products: " + databaseError.getMessage());
            }
        });

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Handle the item click event
                // Here you can pass the selected product to the ItemActivity
                Intent intent = new Intent(FruitActivity.this, ItemActivity.class);
                intent.putExtra("selectedProduct", product);
                startActivity(intent);
            }
        });
    }
}
