package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsers extends AppCompatActivity {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private ContactAdapter contactsAdapter;
    private List<UserModel> userList;
    TextView TVDisplaycategory;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserCategory;
    private String currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);
        TVDisplaycategory = findViewById(R.id.TVcategoryDisplay);
        initializeFirebase();
        setupRecyclerView();

        if (currentUser != null) {
            retrieveUserCategory();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        }
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference("users");
    }

    private void setupRecyclerView() {
        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        contactsAdapter = new ContactAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        contactsAdapter.setOnItemClickListener(user -> {
            Log.d("DisplayUsers", "Clicked on user: " + user.getUsername());
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("recipientEmail", user.getEmail());
            startActivity(intent);
        });
    }

    private void retrieveUserCategory() {
        DatabaseReference currentUserRef = contactsRef.child(currentUser.getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserCategory = snapshot.child("userType").getValue(String.class);
                    currentLocation = snapshot.child("location").getValue(String.class);
                    Log.d("DisplayUsers", "Current user category: " + currentUserCategory);
                    refreshUserList();
                } else {
                    Log.d("DisplayUsers", "User category not found");
                    Toast.makeText(DisplayUsers.this, "User category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUsers.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUserList() {
        if (currentUserCategory == null || currentUserCategory.isEmpty() || currentLocation == null || currentLocation.isEmpty()) {
            return;
        }

        Query userQuery = contactsRef.orderByChild("userType").equalTo(getOppositeCategory(currentUserCategory));
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    UserModel userInfo = contactSnapshot.getValue(UserModel.class);
                    if (userInfo != null && userInfo.getLocation().equals(currentLocation)) {
                        userList.add(userInfo);

                        if (userInfo.getUserType().equals("farmer")) {
                            TVDisplaycategory.setText("Farmers");
                        } else {
                            TVDisplaycategory.setText("Extension Officers");
                        }
                    }
                }
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUsers.this, "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOppositeCategory(String category) {
        return category.equals("farmer") ? "officer" : "farmer";
    }
}