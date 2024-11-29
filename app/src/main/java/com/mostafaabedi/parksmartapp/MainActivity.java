package com.mostafaabedi.parksmartapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

/**
 * The {@code MainActivity} class serves as the main entry point of the ParkSmartApp.
 * It provides a user interface for navigating between different sections of the app
 * using a dropdown menu and buttons.
 */
public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";

    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up event listeners for navigation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize and configure the dropdown menu for navigation
        Spinner menuSpinner = findViewById(R.id.tabMenu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(adapter.getPosition("Home"));

        // Set listener for menu item selection
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id)
            {

                String choice = parent.getItemAtPosition(position).toString();

                switch (choice) {
                    case "About Section": {
                        Intent intent = new Intent(MainActivity.this, About.class);
                        startActivity(intent);
                        break;
                    }
                    case "Find your Parking": {
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Account Profile":
                        handleAccountProfileNavigation();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed when nothing is selected.
            }

        });

        // Set click listener for the "Find Parking" button
        findViewById(R.id.findParkingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Handles navigation to the Account Profile section.
     * Redirects the user to either the {@code AccountActivity} if logged in,
     * or the {@code AccountAccess} activity otherwise.
     */
    private void handleAccountProfileNavigation() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(LOGGED_IN_KEY, false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(MainActivity.this, AccountActivity.class);
        } else {
            intent = new Intent(MainActivity.this, AccountAccess.class);
        }
        startActivity(intent);
    }
}