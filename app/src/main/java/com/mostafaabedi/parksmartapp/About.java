package com.mostafaabedi.parksmartapp;

import android.content.Intent;
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
                        Intent intent = new Intent(About.this, AccountActivity.class);
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
