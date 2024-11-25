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

public class About extends AppCompatActivity {

    private boolean spinnerDefault = false;
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.about_page);

        Spinner menuSpinner = findViewById(R.id.tabMenu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        menuSpinner.setAdapter(adapter);

        menuSpinner.setSelection(adapter.getPosition("About Section"));

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
                        Intent intent = new Intent(About.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if ("Find your Parking".equals(choice))
                    {
                        Intent intent = new Intent(About.this, MapActivity.class);
                        startActivity(intent);
                    }
                    else if ("Account Profile".equals(choice))
                    {
                        handleAccountProfileNavigation();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

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
