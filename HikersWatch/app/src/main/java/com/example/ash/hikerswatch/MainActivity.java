package com.example.ash.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView latTextView;
    private TextView lonTextView;
    private TextView altTextView;
    private TextView accTextView;
    private TextView addressTextView;
    private TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        altTextView = findViewById(R.id.altTextView);
        accTextView = findViewById(R.id.accTextView);
        addressTextView = findViewById(R.id.addressTextView);
        speedTextView = findViewById(R.id.speedTextView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(lastKnownLocation !=null){
                updateLocationInfo(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    public void updateLocationInfo(Location location){

        latTextView.setText("Latitude: "+ Double.toString(location.getLatitude()));
        lonTextView.setText("Longitude: "+ Double.toString(location.getLongitude()));
        accTextView.setText("Accuracy: "+ Double.toString(location.getAccuracy()));
        altTextView.setText("Altitude: "+ Double.toString(location.getAltitude()));
        speedTextView.setText("Speed: " + Double.toString(location.getSpeed()));

        String address = "Sorry couldn't find a address";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listOfAddresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),4);
            if(listOfAddresses!=null && listOfAddresses.size()>0){

                address = "Address: ";

                if(listOfAddresses.get(0).getPostalCode() != null){

                    address+= listOfAddresses.get(0).getPostalCode() +"\n";
                }

                if(listOfAddresses.get(0).getLocality()!=null){

                    address+= listOfAddresses.get(0).getLocality() + " ";
                }
                if(listOfAddresses.get(0).getCountryName()!=null){

                    address+= listOfAddresses.get(0).getCountryName();
                }
                


                addressTextView.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
