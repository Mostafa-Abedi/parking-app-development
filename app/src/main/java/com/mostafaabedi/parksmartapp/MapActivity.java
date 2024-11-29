package com.mostafaabedi.parksmartapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * The {@code MapActivity} class provides a map-based interface for users to
 * find nearby parking lots based on their current location and parking duration input.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ParkMeApiService apiService;

    private EditText durationInput;
    private Button searchButton;

    private boolean spinnerDefault = false;
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String LOGGED_IN_KEY = "isLoggedIn";

    /**
     * Called when the activity is first created.
     * Initializes the map, UI components, and event listeners for user interaction.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize location client and API service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = new ParkMeApiService();

        // Initialize input for parking duration and search button
        durationInput = findViewById(R.id.durationInput);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> fetchUserLocation());

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up navigation menu
        Spinner menuSpinner = findViewById(R.id.tabMenu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(adapter.getPosition("Find your Parking"));

        // Set listener for menu item selection
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (spinnerDefault) {
                    String choice = parent.getItemAtPosition(position).toString();

                    switch (choice) {
                        case "Home": {
                            Intent intent = new Intent(MapActivity.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case "About Section": {
                            Intent intent = new Intent(MapActivity.this, About.class);
                            startActivity(intent);
                            break;
                        }
                        case "Account Profile":
                            handleAccountProfileNavigation();
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
            intent = new Intent(MapActivity.this, AccountActivity.class);
        } else {
            intent = new Intent(MapActivity.this, AccountAccess.class);
        }
        startActivity(intent);
    }

    /**
     * Called when the map is ready to be used.
     * Initializes the map with user location and marker interactions.
     *
     * @param googleMap the Google Map instance.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fetchUserLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            showParkingLotDetails(marker);
            return true;
        });
    }

    /**
     * Fetches the user's current location and updates the map view accordingly.
     */
    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

                        fetchAndDisplayParkingLots(location);
                    } else {
                        Toast.makeText(MapActivity.this, "Unable to get your location.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Fetches and displays nearby parking lots on the map based on user location.
     *
     * @param userLocation the user's current location.
     */
    private void fetchAndDisplayParkingLots(Location userLocation) {
        new Thread(() -> {
            try {
                // Extract user location
                double latitude = userLocation.getLatitude();
                double longitude = userLocation.getLongitude();
                int radius = 500; // Adjust as needed

                // Get user input for parking duration
                String durationStr = durationInput.getText().toString().trim();
                int duration = durationStr.isEmpty() ? 60 : Integer.parseInt(durationStr); // Default 60 minutes

                // Fetch parking data
                JSONObject parkingData = apiService.fetchParkingData(latitude, longitude, radius, duration);
                if (parkingData == null) throw new Exception("No data returned from API.");

                JSONArray parkingLots = parkingData.getJSONArray("result");

                // Calculate average price for markers
                double averagePrice = calculateAveragePrice(parkingLots, duration);

                runOnUiThread(() -> {
                    if (parkingLots.length() == 0) {
                        Toast.makeText(MapActivity.this, "No parking lots found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < parkingLots.length(); i++) {
                        try {
                            JSONObject lot = parkingLots.getJSONObject(i);

                            // Extract parking lot details
                            String name = lot.optString("name", "Unnamed Parking Lot");
                            int spacesTotal = lot.optInt("spacesTotal", 0);
                            String note = lot.optString("note", "No additional details.");
                            JSONArray coordinates = lot.getJSONObject("point").getJSONArray("coordinates");

                            LatLng lotLocation = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));

                            // Extract price for the specified duration
                            double price = extractPriceForDuration(lot, duration);

                            // Determine marker color
                            float color;
                            if (price < averagePrice * 0.8) {
                                color = BitmapDescriptorFactory.HUE_GREEN; // Cheapest
                            } else if (price <= averagePrice * 1.2) {
                                color = BitmapDescriptorFactory.HUE_ORANGE; // Moderate
                            } else if (lot.optString("type").equalsIgnoreCase("Non-restricted")) {
                                color = BitmapDescriptorFactory.HUE_RED; // Expensive
                            } else {
                                color = BitmapDescriptorFactory.HUE_VIOLET; // Non-public
                            }

                            // Create marker snippet
                            String snippet = "Spaces: " + spacesTotal + "\nPrice: $" + price + "\nNote: " + note;

                            // Add marker to map
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(lotLocation)
                                    .title(name)
                                    .snippet(snippet)
                                    .icon(BitmapDescriptorFactory.defaultMarker(color)));

                            // Attach extra data to marker
                            marker.setTag(lot);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MapActivity.this, "Error processing parking lot.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MapActivity.this, "Failed to fetch parking data.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    /**
     * Calculates the average parking price for a given duration across all parking lots.
     *
     * @param parkingLots a JSON array of parking lot data.
     * @param duration    the parking duration in minutes.
     * @return the calculated average price.
     */
    private double calculateAveragePrice(JSONArray parkingLots, int duration) throws Exception {
        double total = 0;
        int count = 0;

        for (int i = 0; i < parkingLots.length(); i++) {
            try {
                JSONObject lot = parkingLots.getJSONObject(i);
                total += extractPriceForDuration(lot, duration);
                count++;
            } catch (Exception ignored) {}
        }

        return count > 0 ? total / count : 0;
    }

    /**
     * Extracts the price for a specific duration from the parking lot JSON data.
     *
     * @param lot      the parking lot JSON object.
     * @param duration the parking duration in minutes.
     * @return the calculated price for the given duration.
     */
    private double extractPriceForDuration(JSONObject lot, int duration) throws Exception {
        JSONArray rates = lot.optJSONArray("structured_rates");
        if (rates == null) return 0;

        for (int i = 0; i < rates.length(); i++) {
            JSONObject rate = rates.getJSONObject(i);
            if (rate.optInt("increment") == duration || duration <= rate.optInt("max", Integer.MAX_VALUE)) {
                return rate.optDouble("rate", 0);
            }
        }
        return 0;
    }

    /**
     * Displays additional details about a selected parking lot.
     *
     * @param marker the selected marker.
     */
    private void showParkingLotDetails(Marker marker) {
        JSONObject lotData = (JSONObject) marker.getTag();
        if (lotData == null) return;

        Intent intent = new Intent(this, ParkingDetailsActivity.class);
        intent.putExtra("lotData", lotData.toString());
        startActivity(intent);
    }
}
