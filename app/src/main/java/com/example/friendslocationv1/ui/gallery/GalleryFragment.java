package com.example.friendslocationv1.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendslocationv1.Config;
import com.example.friendslocationv1.JSONParser;
import com.example.friendslocationv1.Position;
import com.example.friendslocationv1.databinding.FragmentGalleryBinding;
import com.example.friendslocationv1.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Insert(GalleryFragment.this.getActivity()).execute();
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
            builder.setTitle("Downloading");
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