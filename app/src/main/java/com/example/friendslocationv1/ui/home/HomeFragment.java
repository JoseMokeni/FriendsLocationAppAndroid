package com.example.friendslocationv1.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.friendslocationv1.Config;
import com.example.friendslocationv1.JSONParser;
import com.example.friendslocationv1.MyPositionAdapter;
import com.example.friendslocationv1.Position;
import com.example.friendslocationv1.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<Position>();

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnHomeDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download data

                /*JSONParser parser = new JSONParser();
                JSONObject response = parser.makeHttpRequest(Config.URL_GET_ALL,"GET",null);*/

                Download download = new Download(HomeFragment.this.getActivity());
                download.execute();


            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            // Create the adapter
            MyPositionAdapter positionArrayAdapter = new MyPositionAdapter(con, data);

            LinearLayoutManager layoutManager = new LinearLayoutManager(con, LinearLayoutManager.VERTICAL, false);
            binding.recyclerViewPositions.setLayoutManager(layoutManager);
            binding.recyclerViewPositions.setAdapter(positionArrayAdapter);
            //hide dialogue box show result
            alert.dismiss();


        }

    }

}