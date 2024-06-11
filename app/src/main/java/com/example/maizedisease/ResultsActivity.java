package com.example.maizedisease;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView fungicideRecyclerView;
    private FungicideAdapter fungicideAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Set action bar color
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lightgreen)));
        }

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightgreen));
        }

        // Initialize views
        TextView outputTextView = findViewById(R.id.outputTextView);
        fungicideRecyclerView = findViewById(R.id.fungicideRecyclerView);

        // Get references to the DrawerLayout, NavigationView, and its TextViews
        drawerLayout = findViewById(R.id.drawableLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView navigationView = findViewById(R.id.nav_views);
        View headerView = navigationView.getHeaderView(0);
        TextView navProfileTextView = headerView.findViewById(R.id.nav_profile);
        TextView navLogoutTextView = headerView.findViewById(R.id.nav_logout);

        navProfileTextView.setOnClickListener(v -> navigateToProfile());
        navLogoutTextView.setOnClickListener(v -> logout());

        // Retrieve prediction result and fungicide list from intent
        String predictionResult = getIntent().getStringExtra("ResultActivity");
        ArrayList<FungicideModel> fungicideList = getIntent().getParcelableArrayListExtra("fungicideList");
        if (fungicideList == null) {
            fungicideList = new ArrayList<>();
        }

        // Set the prediction result to the outputTextView
        if (predictionResult != null) {
            outputTextView.setText(predictionResult);
        }

        // Set up the RecyclerView and adapter
        fungicideAdapter = new FungicideAdapter(fungicideList);
        fungicideRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fungicideRecyclerView.setAdapter(fungicideAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void navigateToProfile() {
        Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intentProfile = new Intent(ResultsActivity.this, ProfileActivity.class);
            startActivity(intentProfile);
            finish(); // Optionally finish the current activity
        }, 250);
        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
    }

    private void logout() {
        Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intentLogout = new Intent(ResultsActivity.this, SignIn.class);
            startActivity(intentLogout);
            finishAffinity(); // Optionally finish all activities in the task stack
        }, 250);
        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
    }
}