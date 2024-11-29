package com.mostafaabedi.parksmartapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The {@code AccountActivity} class manages the user's account page in the application.
 * It provides functionalities such as viewing the logged-in user's email, logging out,
 * and navigating to the password change screen.
 */
public class AccountActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AccountPrefs";
    private static final String KEY_EMAIL = "email";

    private DatabaseHelper dbHelper;
    private String loggedInEmail;
    private boolean spinnerDefault = false;

    /**
     * Called when the activity is created. Sets up the user interface, initializes components,
     * and handles account-related functionalities.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        dbHelper = new DatabaseHelper(this);

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
                            startActivity(new Intent(AccountActivity.this, MainActivity.class));
                            break;
                        case "About Section":
                            startActivity(new Intent(AccountActivity.this, About.class));
                            break;
                        case "Find your Parking":
                            startActivity(new Intent(AccountActivity.this, MapActivity.class));
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

        // Get references to UI components.
        TextView displayUser = findViewById(R.id.displayUser);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);

        // Retrieve the logged-in email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loggedInEmail = prefs.getString("loggedInEmail", null);

        // Display the logged-in user's email or a default message.
        if (loggedInEmail != null) {
            displayUser.setText("Welcome, " + loggedInEmail);
        } else {
            displayUser.setText("Welcome, Guest");
        }

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("loggedInEmail"); // Clear logged-in email
            editor.putBoolean("isLoggedIn", false); // Mark the user as logged out
            editor.apply();

            Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountActivity.this, AccountAccess.class);
            startActivity(intent);
            finish();
        });

        // Change password functionality
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}
