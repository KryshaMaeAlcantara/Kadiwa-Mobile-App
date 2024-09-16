package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appkadiwa.util.CartItem;
import com.example.appkadiwa.util.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView productNameTextView;
    private ImageView productImageView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;
    private TextView productQuantityTextView;
    private ImageView subtractQuantityImageView;
    private ImageView addQuantityImageView;
    private int quantityValue = 1;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private static final String TAG = "ItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        backButton = findViewById(R.id.backButton);
        productNameTextView = findViewById(R.id.productNameTextView);
        productImageView = findViewById(R.id.productImageView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        subtractQuantityImageView = findViewById(R.id.subtractQuantityImageView);
        addQuantityImageView = findViewById(R.id.addQuantityImageView);
        productQuantityTextView = findViewById(R.id.productQuantityTextView);

        cartItemList = new ArrayList<>();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set click listener for subtractQuantityImageView
        subtractQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityValue > 1) {
                    quantityValue--; // Decrease the quantity value
                    productQuantityTextView.setText(String.valueOf(quantityValue)); // Update the TextView
                }
            }
        });

        // Set click listener for addQuantityImageView
        addQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityValue++; // Increase the quantity value
                productQuantityTextView.setText(String.valueOf(quantityValue)); // Update the TextView
            }
        });

        // Get the selected product's data from the previous activity
        Intent intent = getIntent();
        Product selectedProduct = intent.getParcelableExtra("selectedProduct");

        if (selectedProduct != null) {
            // Set the details of the selected product
            productNameTextView.setText(selectedProduct.getName());
            Picasso.get().load(selectedProduct.getImageUrl()).into(productImageView);
            productDescriptionTextView.setText(selectedProduct.getDescription());
            productPriceTextView.setText(String.format("₱%.2f", selectedProduct.getPrice()));
        }

        db = FirebaseFirestore.getInstance();

        Button addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToCart(selectedProduct);
            }
        });
    }

    private void addItemToCart(Product selectedProduct) {
        String productName = productNameTextView.getText().toString();
        double productPrice = Double.parseDouble(productPriceTextView.getText().toString().replace("₱", "").replace(",", ""));
        int quantity = Integer.parseInt(productQuantityTextView.getText().toString());
        String imageUrl = selectedProduct.getImageUrl();

        // Create the item object
        Map<String, Object> item = new HashMap<>();
        item.put("name", productName);
        item.put("price", productPrice);
        item.put("quantity", quantity);
        item.put("imageUrl", imageUrl);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid).collection("cart").add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ItemActivity.this, "Item added to cart with ID: " + documentReference.getId(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Item added to cart with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ItemActivity.this, "Error adding item to cart",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding item to cart", e);
                });
    }
}
