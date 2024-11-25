package com.mostafaabedi.parksmartapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParkingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);

        // Retrieve parking lot data passed via Intent
        String lotDataJson = getIntent().getStringExtra("lotData");
        if (lotDataJson == null || lotDataJson.isEmpty()) {
            Toast.makeText(this, "No parking data available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONObject lotData = new JSONObject(lotDataJson);

            // Bind UI elements
            TextView title = findViewById(R.id.title);
            TextView details = findViewById(R.id.details);

            // Extract parking lot details
            String name = lotData.optString("name", "Unnamed Parking Lot");
            int spacesTotal = lotData.optInt("spacesTotal", 0);
            String note = lotData.optString("note", "No details available.");
            JSONArray rateCardArray = lotData.optJSONArray("rateCard");
            JSONArray hrsArray = lotData.optJSONArray("hrs");

            // Format rateCard and hrs arrays
            StringBuilder rateCard = new StringBuilder();
            if (rateCardArray != null) {
                for (int i = 0; i < rateCardArray.length(); i++) {
                    rateCard.append(rateCardArray.getString(i)).append("\n");
                }
            } else {
                rateCard.append("No rates available.");
            }

            StringBuilder openHours = new StringBuilder();
            if (hrsArray != null) {
                for (int i = 0; i < hrsArray.length(); i++) {
                    openHours.append(hrsArray.getString(i)).append("\n");
                }
            } else {
                openHours.append("No hours available.");
            }

            // Set values to UI
            title.setText(name);
            details.setText("Spaces: " + spacesTotal +
                    "\n\nNote: " + note +
                    "\n\nRates:\n" + rateCard +
                    "\n\nOpen Hours:\n" + openHours);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading parking details.", Toast.LENGTH_SHORT).show();
        }
    }
}
