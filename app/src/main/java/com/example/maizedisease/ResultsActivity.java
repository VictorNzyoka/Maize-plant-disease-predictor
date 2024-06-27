package com.example.maizedisease;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ResultsActivity extends AppCompatActivity  {
    private RecyclerView fungicideRecyclerView;
    private FungicideAdapter fungicideAdapter;
    private ImageView backButton,logoutButton;
    private LinearLayout home,info,profile,chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        home = findViewById(R.id.homelayout);
        info = findViewById(R.id.infolayout);
        profile = findViewById(R.id.profilelayout);
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);
        chat = findViewById(R.id.chatlayout);


        backButton.setOnClickListener(v -> onBack());
        logoutButton.setOnClickListener(v -> logout());
        home.setOnClickListener(v -> Home());
        profile.setOnClickListener(v -> Profile());
        info.setOnClickListener(v -> information());
        chat.setOnClickListener(v -> messaging());


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

    private void information() {
        Intent intent4 = new Intent(ResultsActivity.this,info.class);
        startActivity(intent4);
    }

    private void Profile() {
        Intent intent5 = new Intent(ResultsActivity.this,ProfileActivity.class);
        startActivity(intent5);
    }

    private void Home() {
        Intent intent6 = new Intent(ResultsActivity.this,MainActivity.class);
        startActivity(intent6);
    }

    private void messaging() {
        Intent intent7 = new Intent(ResultsActivity.this, DisplayUsers.class);
        startActivity(intent7);
    }

    private void logout() {
        Intent intent8 = new Intent(ResultsActivity.this, SignIn.class);
        startActivity(intent8);
    }

    public void onBack() {
        Intent intent9 = new Intent(ResultsActivity.this, MainActivity.class);
        startActivity(intent9);
    }
}