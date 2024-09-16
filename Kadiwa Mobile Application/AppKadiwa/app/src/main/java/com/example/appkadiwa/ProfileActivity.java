package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mesbut, notbut;
    private Button editBut; // Added grantAdminButton
    private ImageView profileImageView;
    private TextView usernameTextView, emailTextView;
    private TextView grantAdminButton;
    private FirebaseAuth mAuth;
    private TextView logoutButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Bind views
        mesbut = findViewById(R.id.chat);
        notbut = findViewById(R.id.notif);
        editBut = findViewById(R.id.edit_button);
        grantAdminButton = findViewById(R.id.regButton1); // Added grantAdminButton
        profileImageView = findViewById(R.id.item_image);
        usernameTextView = findViewById(R.id.username_user);
        emailTextView = findViewById(R.id.email_user);

        // Set OnClickListener for the Edit Profile button
        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, EditActivity.class);
                startActivity(i);
            }
        });

        // Set OnClickListener for the Grant Admin button
        grantAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantAdminRole(); // Call the method to grant admin role
            }
        });

        // Retrieve the current user's UID
        String uid = mAuth.getCurrentUser().getUid();

        // Retrieve the user's data from Firebase Firestore
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve user data from the Firestore document
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");
                            String role = documentSnapshot.getString("role"); // Add this line to retrieve the role

                            // Display the data in the views
                            Picasso.get().load(profileImageUrl).into(profileImageView);
                            usernameTextView.setText(username);
                            emailTextView.setText(email);

                            // Check if the user has the admin role
                            if (role != null && role.equals("admin")) {
                                grantAdminButton.setVisibility(View.GONE); // Hide the Grant Admin button
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });

        // Bind other views and add their click listeners
        mesbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, UserListActivity.class);
                startActivity(i);
            }
        });

        notbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_profile:
                    return true;
                case R.id.bottom_cart:
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });
    }

    // Method to grant admin role to the user
    private void grantAdminRole() {
        String uid = mAuth.getCurrentUser().getUid();

        // Create a data object to update the 'role' field to "admin"
        Map<String, Object> roleData = new HashMap<>();
        roleData.put("role", "admin");

        // Update the 'role' field in the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.update(roleData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Admin role granted", Toast.LENGTH_SHORT).show();
                        grantAdminButton.setVisibility(View.GONE); // Hide the Grant Admin button
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed to grant admin role", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
