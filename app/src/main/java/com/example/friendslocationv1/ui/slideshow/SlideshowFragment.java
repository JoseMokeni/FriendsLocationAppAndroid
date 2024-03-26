package com.example.friendslocationv1.ui.slideshow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.friendslocationv1.Config;
import com.example.friendslocationv1.JSONParser;
import com.example.friendslocationv1.Position;
import com.example.friendslocationv1.R;
import com.example.friendslocationv1.ui.home.HomeFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<Position>();

    GoogleMap map;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            Download download = new Download(getActivity());
            download.execute();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    class Download extends AsyncTask {

        Context con;
        AlertDialog alert;

        public Download(Context con) {
            this.con=con;
        }


        @Override
        protected void onPreExecute() {//ui thread (main thread)
            // show dialogue box
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("Downloading");
            builder.setMessage("Please wait... :)");
            alert = builder.create();
            alert.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {//second thread
            //internet connection => execute service
            data.clear();

            JSONParser parser = new JSONParser();
            JSONObject response = parser.makeHttpRequest(Config.URL_GET_ALL,"GET",null);

            try {
                System.out.println(response.toString());
                int success = response.getInt("success");

                if (success == 0){
                    String message = response.getString("message");

                } else {

                    JSONArray array = response.getJSONArray("positions");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        int id = row.getInt("idposition");
                        String longitude = null;

                        longitude = row.getString("longitude");
                        String latitude = row.getString("latitude");
                        String pseudo = row.getString("pseudo");

                        Position position = new Position(id, longitude, latitude, pseudo);

                        data.add(position);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {//ui thread (main thread)

            if (data.isEmpty()){
                // dismiss dialogue box
                alert.dismiss();
                // show another dialogue box with message "No data found"
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle("No data found");
                builder.setMessage("No data found");
                AlertDialog alert = builder.create();
                alert.show();
                return;
            }

            // add markers to the map
            for (Position position : data) {
                LatLng latLng = new LatLng(Double.parseDouble(position.getLatitude()), Double.parseDouble(position.getLongitude()));
                map.addMarker(new MarkerOptions().position(latLng).title(position.getPseudo()));
            }


            //hide dialogue box show result
            alert.dismiss();


        }

    }
}