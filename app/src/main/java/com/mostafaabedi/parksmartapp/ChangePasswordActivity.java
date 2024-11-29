package com.mostafaabedi.parksmartapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The {@code ChangePasswordActivity} class allows a logged-in user to change their password.
 * It verifies the current password before updating the password in the database.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";
    private boolean spinnerDefault = false;

    /**
     * Called when the activity is created. Initializes the user interface and sets up the
     * password change functionality.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Set up the spinner for navigation.
        Spinner menuSpinner = findViewById(R.id.tabMenu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(adapter.getPosition("Account Profile"));

        // Handle spinner item selection.
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (spinnerDefault) {
                    String choice = parent.getItemAtPosition(position).toString();
                    switch (choice) {
                        case "Home":
                            startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                            break;
                        case "Find your Parking":
                            startActivity(new Intent(ChangePasswordActivity.this, MapActivity.class));
                            break;
                        case "Account Profile":
                            handleAccountProfileNavigation();
                            break;
                        case "About Section":
                            startActivity(new Intent(ChangePasswordActivity.this, About.class));
                            break;

                    }
                } else {
                    spinnerDefault = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed when nothing is selected.
            }
        });

        // Initialize UI components.
        EditText currentPasswordField = findViewById(R.id.currentPasswordField);
        EditText newPasswordField = findViewById(R.id.newPasswordField);
        Button updatePasswordButton = findViewById(R.id.updatePasswordButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Retrieve the logged-in email from SharedPreferences.
        SharedPreferences prefs = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        String loggedInEmail = prefs.getString("loggedInEmail", null);

        // If no user is logged in, prompt to log in again and close the activity.
        if (loggedInEmail == null) {
            Toast.makeText(this, "No account logged in. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up the password update button functionality.
        updatePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordField.getText().toString().trim();
            String newPassword = newPasswordField.getText().toString().trim();

            // Ensure fields are not empty.
            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify the current password and update it if correct.
            Cursor cursor = dbHelper.searchAccountRecords(loggedInEmail);
            if (cursor != null && cursor.moveToFirst()) {
                String dbPassword = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                if (currentPassword.equals(dbPassword)) {
                    dbHelper.updatePassword(loggedInEmail, newPassword);
                    Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show();

                    // Redirect to the AccountActivity after updating the password.
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
    /**
     * Handles navigation to the Account Profile or Account Access screen
     * based on the user's login status.
     */
    private void handleAccountProfileNavigation() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(LOGGED_IN_KEY, false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(ChangePasswordActivity.this, AccountActivity.class);
        } else {
            intent = new Intent(ChangePasswordActivity.this, AccountAccess.class);
        }
        startActivity(intent);
    }
}
