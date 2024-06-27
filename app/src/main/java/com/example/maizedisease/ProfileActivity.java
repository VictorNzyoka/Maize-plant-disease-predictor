package com.example.maizedisease;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;

    // UI elements
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize database references
        if (currentUser != null) {
            mUsersRef = mDatabase.getReference("users").child(currentUser.getUid());
        }

        // Initialize UI elements
        usernameTextView = findViewById(R.id.fnamedisplay);
        emailTextView = findViewById(R.id.emaildisplay);
        phoneTextView = findViewById(R.id.phonedisplay);
        locationTextView = findViewById(R.id.location);

        // Read from the database
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        displayUserDetails(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void displayUserDetails(UserModel userModel) {
        usernameTextView.setText(userModel.getUsername());
        emailTextView.setText(userModel.getEmail());
        phoneTextView.setText(userModel.getPhoneNumber());
        locationTextView.setText(userModel.getLocation());
    }
}

