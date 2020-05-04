package com.example.detective;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CrimeListActivity extends SingleFragmentActivity implements LocationListener {
    private Location locationNo;// Object to save the location
    private LocationManager location;//Object to use the location

    @Override
    protected Fragment createFragment() {
        startLocationService();
        //Bundle to hold the data to pass
        CrimeLab.setmCity(getCity());
        return CrimeListFragment.newInstance();
    }

    private void startLocationService() {//Function to start location service
        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = location.getBestProvider(c, true);//Best provider

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {//if all the required permissions are given then start the location service
            location.requestLocationUpdates(provider, 1, 10, this);
            locationNo = location.getLastKnownLocation(provider); //locationNo get the last known location
        }
    }

    public String getCity() {//Function to get the city of the GPS with object Geocoder
        String city;
        if (locationNo != null) {
            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> address = geocoder.getFromLocation(locationNo.getLatitude(), locationNo.getLongitude(), 1);
                if (!address.isEmpty()) {
                    city = address.get(0).getLocality();
                }
                else {
                    city = getString(R.string.unknown_city);//City is passed the value of locality of geocoder
                }
            } catch (IOException e) {
                city = getString(R.string.unknown_city)+"*";//Error in address list
            }
        }
        else {
            city = getString(R.string.unknown_city)+"/";//Location object is null
        }

        location.removeUpdates(this);
        return city;
    }

    //Functions overrides of location listener
    @Override
    public void onLocationChanged(Location location) {

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

}
