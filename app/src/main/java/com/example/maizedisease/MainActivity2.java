package com.example.maizedisease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        login = findViewById(R.id.loginButton);

        // Set click listener for the login button
        login.setOnClickListener(v -> {
            // Start the MainActivity when the login button is clicked
            startActivity(new Intent(MainActivity2.this, SignIn.class));
        });

    }
}
