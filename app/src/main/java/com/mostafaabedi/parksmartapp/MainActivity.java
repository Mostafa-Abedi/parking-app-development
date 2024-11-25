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

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Spinner menuSpinner = findViewById(R.id.tabMenu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        menuSpinner.setAdapter(adapter);

        menuSpinner.setSelection(adapter.getPosition("Home"));

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id)
            {

                String choice = parent.getItemAtPosition(position).toString();

                if ("About Section".equals(choice))
                {
                    Intent intent = new Intent(MainActivity.this, About.class);
                    startActivity(intent);
                }
                else if ("Find your Parking".equals(choice))
                {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                }
                else if ("Account Profile".equals(choice))
                {
                    handleAccountProfileNavigation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

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