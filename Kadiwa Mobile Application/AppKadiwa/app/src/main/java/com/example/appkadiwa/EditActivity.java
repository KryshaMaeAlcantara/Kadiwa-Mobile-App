package com.example.appkadiwa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CODE = 101;

    private EditText nameEditText, addressEditText, birthdayEditText, genderEditText;
    private ImageView profileImageView;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Bind views
        nameEditText = findViewById(R.id.edit_text_name);
        addressEditText = findViewById(R.id.edit_text_address);
        birthdayEditText = findViewById(R.id.edit_text_birthday);
        genderEditText = findViewById(R.id.edit_text_gender);
        profileImageView = findViewById(R.id.image_view_profile);
        saveButton = findViewById(R.id.button_save);

        // Set an OnClickListener to the profile image ImageView
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CODE);
            }
        });

        // Set an OnClickListener to the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileInformation();
            }
        });

        // Load user profile information
        loadUserProfile();

        // Set an OnClickListener to the birthday EditText field
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void saveProfileInformation() {
        // Retrieve the user's data from the EditText fields
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String birthday = birthdayEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        // Get the current user's UID
        String uid = mAuth.getCurrentUser().getUid();

        // Update the user's Firestore document with the new information
        DocumentReference userRef = db.collection("users").document(uid);

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("address", address);
        profileData.put("birthday", birthday);
        profileData.put("gender", gender);

        userRef.update(profileData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditActivity.this, "Profile information updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditActivity.this, "Failed to update profile information", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile() {
        // Get the current user's UID
        String uid = mAuth.getCurrentUser().getUid();

        // Retrieve the user's Firestore document
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve user data from the Firestore document
                        String name = documentSnapshot.getString("name");
                        String address = documentSnapshot.getString("address");
                        String birthday = documentSnapshot.getString("birthday");
                        String gender = documentSnapshot.getString("gender");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        // Populate the EditText fields with the retrieved data
                        nameEditText.setText(name);
                        addressEditText.setText(address);
                        birthdayEditText.setText(birthday);
                        genderEditText.setText(gender);

                        // Load the profile image using Picasso if available
                        if (profileImageUrl != null) {
                            Picasso.get().load(profileImageUrl).into(profileImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditActivity.this, "Failed to retrieve user profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePickerDialog() {
        // Get current date as the default date for the picker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update the EditText field with the selected date
                        String birthday = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        birthdayEditText.setText(birthday);
                    }
                },
                year,
                month,
                day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Upload the image to Firebase Storage
            StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, retrieve the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Update the user's Firestore document with the new profile image URL
                            String uid = mAuth.getCurrentUser().getUid();
                            DocumentReference userRef = db.collection("users").document(uid);
                            userRef.update("profileImageUrl", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update the UI with the uploaded image if needed
                                        Picasso.get().load(imageUrl).into(profileImageView);
                                        Toast.makeText(EditActivity.this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors that occurred during image upload
                        Toast.makeText(EditActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
