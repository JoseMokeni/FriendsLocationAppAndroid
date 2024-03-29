package com.example.friendslocationv1.ui.gallery;


import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendslocationv1.Config;
import com.example.friendslocationv1.JSONParser;
import com.example.friendslocationv1.MainActivity;
import com.example.friendslocationv1.MapsActivity;
import com.example.friendslocationv1.Position;
import com.example.friendslocationv1.databinding.FragmentGalleryBinding;
import com.example.friendslocationv1.ui.home.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    // location services client
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        binding.btnAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Insert(GalleryFragment.this.getActivity()).execute();
            }
        });

        binding.btnOpenMapAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the map activity
                startActivityForResult(new Intent(GalleryFragment.this.getActivity(), MapsActivity.class), 1);
            }
        });

        binding.btnGetCurrentLocationAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                // check if permissions are granted
                if (!MainActivity.PERMISSION) {
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(GalleryFragment.this.getActivity());
                    builder.setTitle("Permissions");
                    builder.setMessage("Please grant permissions to access location services");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else {
                    // get current location
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(GalleryFragment.this.getActivity(), location -> {
                                if (location != null) {
                                    // get the current location
                                    binding.edLatitudeGallery.setText(String.valueOf(location.getLatitude()));
                                    binding.edLongitudeGallery.setText(String.valueOf(location.getLongitude()));
                                }
                            });

                }

            }
        });

        binding.btnResetGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edPseudoGallery.setText("");
                binding.edLongitudeGallery.setText("");
                binding.edLatitudeGallery.setText("");
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            // get the result
            String result = data.getData().toString();
            String[] parts = result.split(",");
            binding.edLatitudeGallery.setText(parts[0]);
            binding.edLongitudeGallery.setText(parts[1]);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class Insert extends AsyncTask {

        Context con;
        AlertDialog alert;
        String message = "";

        public Insert(Context con) {
            this.con=con;
        }


        @Override
        protected void onPreExecute() {//ui thread (main thread)
            // show dialogue box
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("Inserting");
            builder.setMessage("Please wait... :)");
            alert = builder.create();
            alert.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {//second thread
            //internet connection => execute service
            JSONParser parser = new JSONParser();

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("longitude", binding.edLongitudeGallery.getText().toString());
            params.put("latitude", binding.edLatitudeGallery.getText().toString());
            params.put("pseudo", binding.edPseudoGallery.getText().toString());

            JSONObject response = parser.makeHttpRequest(Config.URL_ADD_POSITION,"POST",params);

            try {
                System.out.println(response.toString());
                int success = response.getInt("success");

                message = response.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {//ui thread (main thread)
            //hide dialogue box show result
            alert.dismiss();

            Toast.makeText(con, message, Toast.LENGTH_SHORT).show();

        }

    }
}