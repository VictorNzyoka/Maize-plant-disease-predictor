package com.example.maizedisease;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UserDetails extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private EditText emailEditText, usernameEditText, phoneEditText, locationEditText, passwordEditText, userTypeEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        phoneEditText = findViewById(R.id.phone_number);
        locationEditText = findViewById(R.id.farmerLocation);
        passwordEditText = findViewById(R.id.password);
        userTypeEditText = findViewById(R.id.user_type);
        submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDetailsToAdmin();
            }
        });

        // Request SMS permission if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        }
    }

    private void sendDetailsToAdmin() {
        String destinationNumber = "+254796175283";
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String userType = userTypeEditText.getText().toString();

        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Email: ").append(email).append("\n");
        messageBody.append("Username: ").append(username).append("\n");
        messageBody.append("Phone: ").append(phone).append("\n");
        messageBody.append("Location: ").append(location).append("\n");
        messageBody.append("Password: ").append(password).append("\n");
        messageBody.append("User Type: ").append(userType);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationNumber, null, messageBody.toString(), null, null);
        Toast.makeText(this, "Details sent successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SMS permission granted
            } else {
                // SMS permission denied
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}