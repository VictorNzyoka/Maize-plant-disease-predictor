package com.example.maizedisease;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView fungicideRecyclerView;
    private FungicideAdapter fungicideAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private static final Map<Integer, Class<?>> menuMap = new HashMap<>();
    static {
        menuMap.put(R.id.nav_home, MainActivity.class);
        menuMap.put(R.id.nav_chat, ChatActivity.class);
        menuMap.put(R.id.nav_profile, ProfileActivity.class);
        menuMap.put(R.id.nav_logout, MainActivity2.class);
    }

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

        // Get references to the DrawerLayout and set up the toggle
        drawerLayout = findViewById(R.id.drawableLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView navigationView = findViewById(R.id.nav_views);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class<?> activityClass = menuMap.get(id);
        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Override onBackPressed to close drawer when back button is pressed
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}