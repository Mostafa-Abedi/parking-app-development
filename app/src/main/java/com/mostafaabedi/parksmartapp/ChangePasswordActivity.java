package com.mostafaabedi.parksmartapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText currentPasswordField = findViewById(R.id.currentPasswordField);
        EditText newPasswordField = findViewById(R.id.newPasswordField);
        Button updatePasswordButton = findViewById(R.id.updatePasswordButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        String loggedInEmail = prefs.getString("loggedInEmail", null);

        if (loggedInEmail == null) {
            Toast.makeText(this, "No account logged in. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updatePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordField.getText().toString().trim();
            String newPassword = newPasswordField.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.searchAccountRecords(loggedInEmail);
            if (cursor != null && cursor.moveToFirst()) {
                String dbPassword = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                if (currentPassword.equals(dbPassword)) {
                    dbHelper.updatePassword(loggedInEmail, newPassword);
                    Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ChangePasswordActivity.this, AccountActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
