package com.example.friendslocationv1;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPositionAdapter extends RecyclerView.Adapter<MyPositionAdapter.MyViewHolder> {

    Context context;

    ArrayList<Position> data;

    public MyPositionAdapter(Context context, ArrayList<Position> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // View creation
        // Création d'un prototype
        // Conversion code XML
        LayoutInflater inf = LayoutInflater.from(context);
        View v = inf.inflate(R.layout.position_row, null);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Affectation des views
        Position p = data.get(position);

        holder.tvPseudo.setText("Pseudo: " + p.getPseudo());
        holder.tvLongitude.setText("Long: " + p.getLongitude());
        holder.tvLatitude.setText("Lat: " + p.getLatitude());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPseudo, tvLongitude, tvLatitude;
        Button btnDelete, btnShowInMap;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPseudo = itemView.findViewById(R.id.textViewPseudoPositionRow);
            tvLongitude = itemView.findViewById(R.id.textViewLongitudePositionRow);
            tvLatitude = itemView.findViewById(R.id.textViewLatitudePositionRow);
            btnDelete = itemView.findViewById(R.id.deleteBtnPositionRow);
            btnShowInMap = itemView.findViewById(R.id.showInMapBtnPositionRow);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Delete button fired", Toast.LENGTH_SHORT).show();
                }
            });
            
            btnShowInMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Show in map fired", Toast.LENGTH_SHORT).show();
                }
            });


        }

        /*class Delete extends AsyncTask {

            Context con;
            AlertDialog alert;
            String message = "";

            public Delete(Context con) {
                this.con = con;
            }


            @Override
            protected void onPreExecute() {//ui thread (main thread)
                // show dialogue box
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle("Deleting");
                builder.setMessage("Please wait... :)");
                alert = builder.create();
                alert.show();

            }

            @Override
            protected Object doInBackground(Object[] objects) {//second thread
                //internet connection => execute service
                JSONParser parser = new JSONParser();

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("id", binding.edLongitudeGallery.getText().toString());

                JSONObject response = parser.makeHttpRequest(Config.URL_ADD_POSITION, "POST", params);

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

        }*/
    }


}
