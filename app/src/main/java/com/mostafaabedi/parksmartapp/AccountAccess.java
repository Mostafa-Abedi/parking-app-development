package com.mostafaabedi.parksmartapp;

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
 * The {@code AccountAccess} class manages the account access functionality for the application.
 * It allows users to sign up, log in, and navigate to other sections of the app.
 */
public class AccountAccess extends AppCompatActivity {

    private boolean spinnerDefault = false;
    private DatabaseHelper dbHelper;

    /**
     * Called when the activity is created. Sets up the layout, initializes navigation,
     * and handles account access functionality.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_access);

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
                            startActivity(new Intent(AccountAccess.this, MainActivity.class));
                            break;
                        case "About Section":
                            startActivity(new Intent(AccountAccess.this, About.class));
                            break;
                        case "Find your Parking":
                            startActivity(new Intent(AccountAccess.this, MapActivity.class));
                            break;
                    }
                } else {
                    spinnerDefault = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required when no selection is made.
            }
        });

        // Get references to UI components.
        Button signUpButton = findViewById(R.id.signUpButton);
        Button logInButton = findViewById(R.id.logInButton);
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);

        // Handle sign-up button click.
        signUpButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.searchAccountRecords(email);
            if (cursor != null && cursor.moveToFirst()) {
                Toast.makeText(this, "Account already exists.", Toast.LENGTH_SHORT).show();
                cursor.close();
            } else {
                dbHelper.insertAccount(email, password);

                // Save email to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("AccountPrefs", MODE_PRIVATE).edit();
                editor.putString("loggedInEmail", email);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Intent intent = new Intent(AccountAccess.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle log-in button click.
        logInButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.searchAccountRecords(email);
            if (cursor != null && cursor.moveToFirst()) {
                String dbPassword = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                if (password.equals(dbPassword)) {
                    // Save email to SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("AccountPrefs", MODE_PRIVATE).edit();
                    editor.putString("loggedInEmail", email);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Intent intent = new Intent(AccountAccess.this, AccountActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(this, "Account not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
