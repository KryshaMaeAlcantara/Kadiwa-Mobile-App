package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.appkadiwa.util.Image;
import com.example.appkadiwa.util.ImageAdapter;
import com.example.appkadiwa.util.Product;
import com.example.appkadiwa.util.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Image> imageList;
    private FirebaseFirestore db;
    private RecyclerView recView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    private ImageView frucat, vegcat, whecat, mecat, seacat, mesbut, notbut;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mesbut = findViewById(R.id.chat);
        notbut = findViewById(R.id.notif);

        notbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,NotificationActivity.class);
                startActivity(i);
            }

        });

        mesbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,UserListActivity.class);
                startActivity(i);
            }

        });

        frucat = findViewById(R.id.fruit);
        vegcat = findViewById(R.id.vegetables);
        whecat = findViewById(R.id.wheat);
        mecat = findViewById(R.id.meat);
        seacat = findViewById(R.id.seafood);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
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

        frucat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,FruitActivity.class);
                startActivity(i);
            }

        });

        vegcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,VegetableActivity.class);
                startActivity(i);
            }

        });

        whecat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,WheatActivity.class);
                startActivity(i);
            }

        });

        mecat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,MeatActivity.class);
                startActivity(i);
            }

        });

        seacat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new
                        Intent(HomeActivity.this,SeafoodActivity.class);
                startActivity(i);
            }

        });

        // Initialize the RecyclerView, layout manager, and adapter
        recView= findViewById(R.id.rec_rec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);

        // Set the layout manager and adapter to the RecyclerView
        recView.setLayoutManager(new GridLayoutManager(this, 3));
        recView.setAdapter(productAdapter);

        // Query the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("products");
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

        recyclerView = findViewById(R.id.recyclerView);
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imageAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve image URLs from Firestore
        CollectionReference imagesRef = db.collection("images");
        imagesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }

                imageList.clear();

                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Image imageModel = document.toObject(Image.class);
                    imageList.add(imageModel);
                }

                imageAdapter.notifyDataSetChanged();
            }
        });

        // Set click listener on the product adapter
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Handle the item click event
                // Here you can pass the selected product to the ItemActivity
                Intent intent = new Intent(HomeActivity.this, ItemActivity.class);
                intent.putExtra("selectedProduct", product);
                startActivity(intent);
            }
        });
    }
}







