package com.mostafaabedi.parksmartapp;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ParkMeApiService {

    private static final String AUTH_BASE_URL = "https://api.iq.inrix.com/auth/v1/";
    private static final String PARKING_API_URL = "https://api.iq.inrix.com/lots/v3";
    private static final String APP_ID = "iiu2xho2ic"; // Your App ID
    private static final String HASH_TOKEN = "aWl1MnhobzJpY3xmUDZldTFrdjVFdkFaY1k2SlQ3MjNySUZzRFZXUnBrOVFnR2d5MWdm"; // Provided hash token

    private String accessToken;
    private long tokenExpiryTime; // Tracks token expiry to reauthenticate if needed
    private final OkHttpClient httpClient;

    public ParkMeApiService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Authenticates and retrieves an access token from the INRIX API.
     */
    public synchronized void authenticate() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            System.out.println("Using cached access token.");
            return; // Token is still valid
        }

        HttpUrl url = HttpUrl.parse(AUTH_BASE_URL + "appToken")
                .newBuilder()
                .addQueryParameter("appId", APP_ID)
                .addQueryParameter("hashToken", HASH_TOKEN)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")
                .build();

        System.out.println("Authenticating with INRIX API: " + url);

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject responseBody = new JSONObject(response.body().string());
                this.accessToken = responseBody.getJSONObject("result").getString("token");
                long expiresIn = 60 * 60 * 1000; // Assuming token is valid for 1 hour
                this.tokenExpiryTime = System.currentTimeMillis() + expiresIn;

                System.out.println("Authentication successful. Access Token: " + accessToken);
            } else {
                throw new IOException("Failed to authenticate: " + response.message());
            }
        }
    }

    /**
     * Fetches parking lots near the specified latitude and longitude with optional duration.
     *
     * @param latitude  Latitude of the search center.
     * @param longitude Longitude of the search center.
     * @param radius    Search radius in meters.
     * @param duration  Duration in minutes for parking rates (optional).
     * @return A JSONObject containing parking lot data.
     * @throws Exception If an error occurs during the request.
     */
    public JSONObject fetchParkingData(double latitude, double longitude, int radius, int duration) throws Exception {
        authenticate(); // Ensure the token is valid

        HttpUrl.Builder urlBuilder = HttpUrl.parse(PARKING_API_URL)
                .newBuilder()
                .addQueryParameter("point", latitude + "|" + longitude)
                .addQueryParameter("radius", String.valueOf(radius))
                .addQueryParameter("locale", "en-US");

        // Include parking duration if provided
        if (duration > 0) {
            urlBuilder.addQueryParameter("duration", String.valueOf(duration));
        }

        HttpUrl url = urlBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        System.out.println("Fetching parking data from: " + url);

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Parking Data Response: " + responseBody);
                return new JSONObject(responseBody);
            } else {
                System.out.println("Failed to fetch parking data. Response: " + response.message());
                throw new IOException("Failed to fetch parking data: " + response.message());
            }
        }
    }
}
