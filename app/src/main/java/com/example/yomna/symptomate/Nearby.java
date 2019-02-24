package com.example.yomna.symptomate;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Nearby extends AppCompatActivity {

    DatabaseReference mDatabase;
    List<Double> locations_lat;
    List<Double> locations_lon;
    List<String> dis;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        locations_lat = new ArrayList<>();
        locations_lon = new ArrayList<>();
        dis = new ArrayList<>();

        tv = findViewById(R.id.my_text_nearby);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.android_id);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                   // for(DataSnapshot ds_child: ds.getChildren()) {
                        //HistoryContent h = ds_child.getValue(HistoryContent.class);
                    HistoryContent h = ds.getValue(HistoryContent.class);
                        //Log.d("asd", "12132456,!!" + MainActivity.long_d + " : " + h.getLongitude());

                        if(h.getLatitude() != null && h.getLongitude() != null) {
                            float[] dist = new float[1];

                            try {
                                Random rand = new Random();
                                double n = rand.nextInt(10) + 1;
                                n = n/150.0;
                                //Log.d("asd", "12132456,!!--> " + MainActivity.long_d + " : " + h.getLongitude());
                                Location.distanceBetween(MainActivity.lat_d, MainActivity.long_d, Double.parseDouble(h.getLatitude())+n, Double.parseDouble(h.getLongitude())+n,dist);
                            }
                            catch (Exception e) {
                                Log.d("asd", "12132456,!!" + e.toString());
                            }

                            if(dist[0]/1000 > 1){
                                Log.d("asd", "12132456,!!--> " + MainActivity.long_d + " : " + h.getLongitude());
                                //1km radius
                                locations_lat.add(Double.parseDouble(h.getLatitude()));
                                locations_lon.add(Double.parseDouble(h.getLongitude()));
                                dis.add(h.getDiagnosis());
                            }
                      //  }
                    }

                }

                String put_st = "";

                if(dis.size() > 0) {
                    for(int i=0; i<dis.size(); ++i) {
                        Random rand = new Random();
                        double n = rand.nextInt(10) + 1;
                        n = n/150.0;
                        put_st += "Latitude: \t" + Double.toString(locations_lat.get(i)+n)+"\n";
                        put_st += "Longitude: \t" + Double.toString(locations_lon.get(i)+n)+"\n";
                        put_st += "Possible Diseases: " + dis.get(i);
                        put_st += "\n\n******************************\n";
                    }

                    tv.setText(put_st);
                }
                else {
                    tv.setText("No Nearby data to show!");
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
