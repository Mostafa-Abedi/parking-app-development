package com.mostafaabedi.parksmartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private boolean spinnerDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        Spinner menuSpinner = findViewById(R.id.tabMenu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        menuSpinner.setAdapter(adapter);

        menuSpinner.setSelection(adapter.getPosition("Account Profile"));

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id)
            {
                if (spinnerDefault)
                {
                    String choice = parent.getItemAtPosition(position).toString();

                    if ("Home".equals(choice))
                    {
                        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if ("About Section".equals(choice))
                    {
                        Intent intent = new Intent(AccountActivity.this, About.class);
                        startActivity(intent);
                    }
                    else if ("Find your Parking".equals(choice))
                    {
                        Intent intent = new Intent(AccountActivity.this, MapActivity.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    spinnerDefault = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

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
