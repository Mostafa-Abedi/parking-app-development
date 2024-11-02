package com.mostafaabedi.parksmartapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ParkMeApiService {
    @GET("parking-lots")
    Call<List<ParkingLot>> getParkingLots(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("key") String apiKey
    );
}
