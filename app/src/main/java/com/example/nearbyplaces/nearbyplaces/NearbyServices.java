package com.example.nearbyplaces.nearbyplaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NearbyServices {
    @GET
    Call<NearbyPlaces> getNearbyPlaces(@Url String endUrl);
}
