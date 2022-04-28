package com.abhi.howfarami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnScan;
    TextView tvOutput1, tvOutput2, tvOutput3;
    double [] myLatLong = new double[2];//0->lat,1->long
    double default_lat=22.0512423, default_long=88.0785574;


    //loc 1 is set dependencies in project->app->open module setting->dependencies->+->play-services-location
    //loc2
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);
        tvOutput1 = findViewById(R.id.tvOutput1);
        tvOutput2 = findViewById(R.id.tvOutput2);
        tvOutput3 = findViewById(R.id.tvOutput3);

        //loc 3...initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              if location is not off
                tvOutput1.setText(String.valueOf(myLatLong[0]));
                tvOutput2.setText(String.valueOf(myLatLong[1]));

                distance();
//                else make the location on
            }
        });



    }
//============================================================================================================
    private void distance() {

//        calculate longitude difference
        double longDiff = default_long-myLatLong[1];
//        calculate distance
        double distance = Math.sin(deg2rad(default_lat))
                        * Math.sin(deg2rad(myLatLong[0]))
                        + Math.cos(deg2rad(default_lat))
                        * Math.cos(deg2rad(myLatLong[0]))
                        * Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);
//        convert distance radian to degree
        distance = rad2deg(distance);
//        distance in miles
        distance = distance * 60 * 1.1515;
//        distance in kilometers
        distance = distance * 1.609344;
        tvOutput3.setText(String.format(Locale.US, "You are %2f km. far from Abhirup",distance));
    }

    //convert radian to degree
    private double rad2deg(double distance) {

        return (distance*180.0/Math.PI);
    }

    //convert degree to radian
    private double deg2rad(double default_lat_parameter) {
        return (default_lat_parameter*Math.PI/180.0);

    }
//=============================================================================================================
    @Override
    protected void onStart() {
        super.onStart();
        //here
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            askLocationPermission();
        }
    }

    private void getLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location!= null){


                    myLatLong[0]=location.getLatitude();
                    myLatLong[1]=location.getLongitude();
//                    tvOutput1.setText(String.valueOf(location.getLatitude()));
//                    tvOutput2.setText(String.valueOf(location.getLongitude()));

                }

            }

        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tvOutput1.setText("error");
                tvOutput2.setText("error");
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });

    }


    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
        PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "askLocationPermission: You should show an alert dialog");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //PERMISSION GRANTED
                getLocation();
            } else {
                //permission not granted
            }
        }
    }
}
