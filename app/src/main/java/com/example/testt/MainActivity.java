package com.example.testt;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity{

    private FusedLocationProviderClient fusedLocationClient;
    private String latitudeString;
    private String longitudeString;
    private TextView Coordinates;
    private TextView URL;
    private TextView Rainy;
    private EditText thing;
    private ImageView portrait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Coordinates = findViewById(R.id.CoordinateTextView);
        URL = findViewById(R.id.textView3);
        Rainy = findViewById(R.id.IsItRainy);
        thing = findViewById(R.id.TestText);
        portrait = findViewById(R.id.imageView);
        portrait.setImageResource(R.drawable.sleep);

    }

    public void executeMission(View view)  {
        portrait.setImageResource(R.drawable.connecting);
        Rainy.setText("Connecting...");
        getCoordinates();
    }

    public void getCoordinates() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null) {
                            latitudeString = String.valueOf(location.getLatitude());
                            longitudeString = String.valueOf(location.getLongitude());
                            Coordinates.setText(latitudeString + ", " + longitudeString);

                            displayWeatherData();

                        } else {
                            Coordinates.setText("yikes");
                        }

                    }
                });
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Coordinates.setText("fuck");
//                    }
//                });
    }

    public void displayWeatherData() {
        String todaysurl = "https://api.darksky.net/forecast/48a7274ddca0e265e31103b1eaf48b09/" + latitudeString + "," + longitudeString;
        thing.setText(todaysurl);

        new RetrieveWeatherData().execute(todaysurl);
    }

    public void processWeatherData(Boolean isitRainy) {

        try {
            if (isitRainy) {
                Rainy.setText("It will rain, take an umbrella.");
                portrait.setImageResource(R.drawable.rainy);
            } else {
                Rainy.setText("No rain, no umbrella!");
                portrait.setImageResource(R.drawable.sunny);
            }
        } catch (Exception e) {
            thing.setText(e.toString());
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{INTERNET}, 1);
    }

    private class RetrieveWeatherData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... url) {
            BufferedReader reader;
            try {
                URL weatherurl = new URL(url[0]);
                reader = new BufferedReader(new InputStreamReader(weatherurl.openStream()));
                String inputline;
                String read = "";
                while ((inputline = reader.readLine()) != null) {
                    read += inputline;
                }

                return TakeOutData(read);

            } catch (Exception e) {
                final Exception ee = e;
                runOnUiThread(new Runnable() {
                    Exception eee = ee;
                    @Override
                    public void run() {

                        Rainy.setText(eee.toString());
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            processWeatherData(bool);
        }

        private Boolean TakeOutData(String initialD) {
            String currentString = initialD;
            int hourlyindex = initialD.indexOf("\"hourly\"");
            currentString = currentString.substring(hourlyindex);
            int dataIndex = currentString.indexOf("data");
            currentString = currentString.substring(0, dataIndex);
            int rain = 0;

            final String ee = Integer.toString(dataIndex);
/*            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Coordinates.setText(ee);
                }
            });*/

            for (char ch : currentString.toCharArray()) {
                if ((ch == 'r') && (rain == 0)) {
                    rain = 1;
                } else
                if ((ch == 'a') && (rain == 1)) {
                    rain = 2;
                } else
                if ((ch == 'i') && (rain == 2)) {
                    rain = 3;
                } else
                if ((ch == 'n') && (rain == 3)) {
                    return true;
                } else {
                    rain = 0;
                }

            }
            return false;
        }

    }


}
