package com.example.nearbyplaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nearbyplaces.nearbyplaces.NearbyPlaces;
import com.example.nearbyplaces.nearbyplaces.NearbyServices;
import com.example.nearbyplaces.nearbyplaces.Result;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PLACE_PICKER_REQUEST = 1;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private ClusterManager<myitem> clusterManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng BDBL = new LatLng(23.750854, 90.393527);
       /* mMap.addMarker(new MarkerOptions().position(BDBL).title("BDBL Bhaban")
                .snippet("12 Kazi Nazrul Islam Ave, Kawranbazar, Dhaka"));*/
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BDBL, 13f));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},111);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getNearbyPlaces();
    }

    private void getNearbyPlaces() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NearbyServices nearbyService = retrofit.create(NearbyServices.class);
        String apikey = getString(R.string.near_by_place_api);
        String endUrl = String.format("place/nearbysearch/json?location=23.750854,90.393527&radius=500&type=restaurant&key=%s",apikey);
        nearbyService.getNearbyPlaces(endUrl)
                .enqueue(new Callback<NearbyPlaces>() {
                    @Override
                    public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                        if(response.isSuccessful()){
                            NearbyPlaces placeResponse = response.body();
                            List<Result>results = placeResponse.getResults();
                            Toast.makeText(MapsActivity.this, "size: "+results.size(), Toast.LENGTH_SHORT).show();
                            for(Result result : results){
                                double lat = result.getGeometry().getLocation().getLat();
                                double lng = result.getGeometry().getLocation().getLng();
                                LatLng latLng = new LatLng(lat, lng);
                                /*mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(result.getName()).snippet(result.getVicinity()));*/
                                myitem item = new myitem(lat, lng, result.getName(), result.getVicinity());
                                clusterManager.addItem(item);
                                clusterManager.cluster();

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<NearbyPlaces> call, Throwable t) {

                    }

                });
    }

    public void showPlacePickerDialog(View view) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Toast.makeText(this, place.getAddress().toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Toast.makeText(this, place.getAddress().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchPlaces(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("BD")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
}
