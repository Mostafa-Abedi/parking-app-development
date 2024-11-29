package com.mostafaabedi.parksmartapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Activity to display detailed information about a parking lot, including availability,
 * capacity, amenities, rates, and contact details.
 */
public class ParkingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            TextView availableSpots = findViewById(R.id.available_spots);
            TextView totalCapacity = findViewById(R.id.total_capacity);
            TextView note = findViewById(R.id.note);
            TextView rates = findViewById(R.id.rates);
            TextView openHours = findViewById(R.id.open_hours);
            TextView evCharging = findViewById(R.id.ev_charging);
            TextView address = findViewById(R.id.address);
            TextView phone = findViewById(R.id.phone);

            // Extract parking lot details
            String name = lotData.optString("name", "Unnamed Parking Lot");
            int available = lotData.optJSONObject("occupancy").optInt("available", 0);
            int spacesTotal = lotData.optInt("spacesTotal", 0);
            String noteText = lotData.optString("note", "No details available.");
            JSONArray amenitiesArray = lotData.optJSONArray("amenities");
            JSONArray rateCardArray = lotData.optJSONArray("rateCard");
            JSONArray hrsArray = lotData.optJSONArray("hrs");

            // Set toolbar title
            getSupportActionBar().setTitle(name);

            // Display availability and capacity
            availableSpots.setText("Available Spots: " + available);
            totalCapacity.setText("Capacity: " + spacesTotal);

            // Display note
            if (noteText == "null") {
                note.setText("Note:\nNo details available.");
            } else {
                note.setText("Note:\n" + noteText);
            }

            // Format and display rates
            StringBuilder rateCard = new StringBuilder();
            if (rateCardArray != null && rateCardArray.length() > 0) {
                for (int i = 0; i < rateCardArray.length(); i++) {
                    rateCard.append(rateCardArray.getString(i));
                    if (i < rateCardArray.length() - 1) { // Append newline only if not the last item
                        rateCard.append("\n");
                    }
                }
            } else {
                rateCard.append("No rates available.");
            }
            rates.setText("Rates:\n" + rateCard.toString());

            // Format and display open hours
            StringBuilder openHoursText = new StringBuilder();
            if (hrsArray != null && hrsArray.length() > 0) {
                for (int i = 0; i < hrsArray.length(); i++) {
                    openHoursText.append(hrsArray.getString(i));
                    if (i < hrsArray.length() - 1) { // Append newline only if not the last item
                        openHoursText.append("\n");
                    }
                }
            } else {
                openHoursText.append("No hours available.");
            }
            openHours.setText("Hours:\n" + openHoursText.toString());


            // Check for EV Chargers in amenities
            boolean evChargingAvailable = false;
            if (amenitiesArray != null) {
                for (int i = 0; i < amenitiesArray.length(); i++) {
                    JSONObject amenity = amenitiesArray.getJSONObject(i);
                    if (amenity.optString("name").equalsIgnoreCase("EV Chargers")) {
                        evChargingAvailable = amenity.optBoolean("value", false);
                        break;
                    }
                }
            }

            // Display EV charging availability
            if (evChargingAvailable) {
                evCharging.setText("EV Charging: Available");
                evCharging.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                evCharging.setText("EV Charging: Not available");
                evCharging.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            // Set clickable address
            JSONObject addressObject = lotData.optJSONObject("navigationAddress");
            if (addressObject != null) {
                String fullAddress = addressObject.optString("street", "Unknown Address") + ", "
                        + addressObject.optString("city", "Unknown City") + ", "
                        + addressObject.optString("state", "Unknown State") + " "
                        + addressObject.optString("postal", "");
                address.setText("Address:\n" + fullAddress);
                address.setOnClickListener(v -> {
                    String mapQuery = Uri.encode(fullAddress);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + mapQuery));
                    startActivity(mapIntent);
                });
            }

            // Set clickable phone number
            JSONArray phonesArray = lotData.optJSONArray("phones");
            if (phonesArray != null && phonesArray.length() > 0) {
                String phoneNumber = phonesArray.getJSONObject(0).optString("number", "Unknown");
                phone.setText("Phone:\n" + phoneNumber);
                phone.setOnClickListener(v -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading parking data.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Handles the "Up" navigation action in the toolbar.
     *
     * @return {@code true} to indicate the action was handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
