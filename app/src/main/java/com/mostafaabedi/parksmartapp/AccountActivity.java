package com.mostafaabedi.parksmartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        // Initialize buttons from the layout
        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up listener for the "Edit Profile" button
        editProfileButton.setOnClickListener(v -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Set up listener for the "Change Password" button
        changePasswordButton.setOnClickListener(v -> {
            // Navigate to ChangePasswordActivity
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Set up listener for the "Logout" button
        logoutButton.setOnClickListener(v -> {
            // Navigate to LoginActivity after logging out
            // Optionally, you can clear user session data here
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the AccountActivity
        });
    }
}
