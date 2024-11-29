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

/**
 * The {@code About} class represents the "About" section of the application.
 */
public class About extends AppCompatActivity {

    private boolean spinnerDefault = false;
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";


    /**
     * Called when the activity is created. Sets up the layout, initializes the dropdown menu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.about_page);

        // Set up the spinner for navigation.
        Spinner menuSpinner = findViewById(R.id.tabMenu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);

        // Set default selection to "About Section".
        menuSpinner.setSelection(adapter.getPosition("About Section"));

        // Handle item selection in the spinner.
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (spinnerDefault) {
                    String choice = parent.getItemAtPosition(position).toString();

                    switch (choice) {
                        case "Home": {
                            Intent intent = new Intent(About.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case "Find your Parking": {
                            Intent intent = new Intent(About.this, MapActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case "Account Profile":
                            handleAccountProfileNavigation();
                            break;
                    }
                }
                else {
                    spinnerDefault = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required when no selection is made.
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
            intent = new Intent(About.this, AccountActivity.class);
        } else {
            intent = new Intent(About.this, AccountAccess.class);
        }
        startActivity(intent);
    }
}
