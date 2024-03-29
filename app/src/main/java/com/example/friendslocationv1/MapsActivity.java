package com.example.friendslocationv1;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.friendslocationv1.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityMapsBinding binding;

    private LatLng latLng;

    LocationCallback locationCallback;
    boolean requestingLocationUpdate = false;

    private FusedLocationProviderClient fusedLocationClient;


    LatLng currentPosition;

    Marker currentPositionMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestingLocationUpdate) {
            startLocationUpdate();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        if (!MainActivity.PERMISSION) {
            // show an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permissions");
            builder.setMessage("Please grant permissions to get current location updates");
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            int LOCATION_UPDATE_INTERVAL = 10000;
            int LOCATION_UPDATE_FASTEST_INTERVAL = 5000;
            int LOCATION_UPDATE_SMALLEST_DISPLACEMENT = 10;

            LocationRequest locationRequest = new LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMinUpdateDistanceMeters(LOCATION_UPDATE_SMALLEST_DISPLACEMENT)
                    .setMinUpdateIntervalMillis(LOCATION_UPDATE_FASTEST_INTERVAL)
                    .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        currentPositionMarker.setPosition(currentPosition);
                        map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                    }
                }
            };


            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            requestingLocationUpdate = true;

        }







    }

    private void stopLocationUpdate() {
        if (requestingLocationUpdate) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdate = false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MapsActivity.this.latLng = latLng;
                // delete all markers except currentPositionMarker
                map.clear();
                currentPositionMarker = googleMap.addMarker(new MarkerOptions().position(currentPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("You are here"));


                // create marker
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latLng.latitude, latLng.longitude)).title("New Marker");
                // adding marker
                map.addMarker(marker);


            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // show a dialog do you want to save this location?
                // if yes, send result to the parent activity

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Save Location");
                builder.setMessage("Do you want to save this location?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent resultIntent = new Intent();
                        resultIntent.setData(Uri.parse(latLng.latitude + "," + latLng.longitude));

                        setResult(1, resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

                return false;
            }
        });

        // set a different marker on current position
        // a label should be visible saying "You are here"
        // the marker should be a different color

        // get current position
        // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentPosition = new LatLng(-34, 151);
        if (!MainActivity.PERMISSION) {
            // show an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permissions");
            builder.setMessage("Please grant permissions to display your current location on the map");
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            // get current location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            }
                    );

            // start location updates
            startLocationUpdate();

        }

        currentPositionMarker = googleMap.addMarker(new MarkerOptions().position(currentPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("You are here"));

        // check if the intent has a position
        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            latLng = intent.getParcelableExtra("position");
            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latLng.latitude, latLng.longitude)).title("New Marker");
            // adding marker
            map.addMarker(marker);

            // move the camera to the position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        } else {
            // move the camera to the current position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        }

    }
}