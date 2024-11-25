package com.mostafaabedi.parksmartapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ParkMeApiService apiService;
    private boolean spinnerDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize location client and API service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = new ParkMeApiService();

        // Spinner setup
        Spinner menuSpinner = findViewById(R.id.tabMenu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(adapter.getPosition("Find your Parking"));

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerDefault) {
                    String choice = parent.getItemAtPosition(position).toString();
                    handleMenuSelection(choice);
                } else {
                    spinnerDefault = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void handleMenuSelection(String choice) {
        Intent intent;
        switch (choice) {
            case "Home":
                intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case "About Section":
                intent = new Intent(MapActivity.this, About.class);
                startActivity(intent);
                break;
            case "Account Profile":
                intent = new Intent(MapActivity.this, AccountActivity.class);
                startActivity(intent);
                break;
        }
    }

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

    private void fetchAndDisplayParkingLots(Location userLocation) {
        new Thread(() -> {
            try {
                // Extract user location
                double latitude = userLocation.getLatitude();
                double longitude = userLocation.getLongitude();
                int radius = 550; // Set radius to match the example request

                System.out.println("Requesting parking data: Lat=" + latitude + ", Lng=" + longitude + ", Radius=" + radius);

                // Fetch parking data using the API service
                JSONObject parkingData = apiService.fetchParkingData(latitude, longitude, radius);
                if (parkingData == null) {
                    throw new Exception("No data returned from API.");
                }

                // Debugging: Log the raw API response
                System.out.println("Raw Response: " + parkingData.toString(2));

                JSONArray parkingLots = parkingData.getJSONArray("result");

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

                            LatLng lotLocation = new LatLng(
                                    coordinates.getDouble(1), // Latitude
                                    coordinates.getDouble(0)  // Longitude
                            );

                            // Create marker snippet with details
                            String snippet = "Spaces: " + spacesTotal + "\nNote: " + note;

                            // Add marker to map
                            mMap.addMarker(new MarkerOptions()
                                    .position(lotLocation)
                                    .title(name)
                                    .snippet(snippet));

                            // Log added marker details
                            System.out.println("Added marker: " + name + " at " + lotLocation.latitude + ", " + lotLocation.longitude);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MapActivity.this, "Error processing a parking lot.", Toast.LENGTH_SHORT).show();
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




    private void showParkingLotDetails(Marker marker) {
        String parkingLotInfo = marker.getTitle() + "\n" + marker.getSnippet();
        Toast.makeText(this, parkingLotInfo, Toast.LENGTH_LONG).show();
    }

    private void navigateToParkingLot(LatLng location) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    fetchUserLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}