package com.yaronfuks.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView allInfoTextView;
    private Button sosButton;
    private int altitude;


    public void updateLocationInfo(final Location location) {

        allInfoTextView = findViewById(R.id.all_info);
        sosButton = findViewById(R.id.sosButton);
         String address = "Could not find address";


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
        try {

            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addressList != null && addressList.size() > 0) {

                address = "\n";
                if (addressList.get(0).getSubThoroughfare() != null) {

                    address += addressList.get(0).getSubThoroughfare() + " ";
                }


                if (addressList.get(0).getThoroughfare() != null) {

                    address += addressList.get(0).getThoroughfare() + "\n";
                }

                if (addressList.get(0).getLocality() != null) {

                    address += addressList.get(0).getLocality() + ", ";
                }

//                if (addressList.get(0).getPostalCode() != null) {
//
//                    address += addressList.get(0).getPostalCode() + "\n";
//                }

                if (addressList.get(0).getCountryName() != null) {

                    address += addressList.get(0).getCountryName() + "\n";
                }

            }

            final String addressToSend = address;

            altitude = (int) location.getAltitude();

            allInfoTextView.setText("Latitude: " + location.getLatitude() + "\n"
                    + "Longitude: " + location.getLongitude() + "\n"
                  //  + "Altitude: " + location.getAltitude() + "\n"
                    + "Altitude: " + altitude + "\n"
                    + "Address: " + address);



            sosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String shareBody = "I NEED HELP!  My Location is: \n Latitude: " + location.getLatitude()
                            + "\n Longitude: " + location.getLongitude()
                            + "\n Address: " + addressToSend;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SOS Location Message");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, null));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }

    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            startListening();

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {

                    updateLocationInfo(location);
                }


            }

        }


    }
}
