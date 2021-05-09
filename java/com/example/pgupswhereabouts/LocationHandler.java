package com.example.pgupswhereabouts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.Executor;

public class LocationHandler {
    private Service activity;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private OnLocationUpdateListener onLocationUpdateListener;
    private boolean updateStartedInternally = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public LocationHandler(Service activity, OnLocationUpdateListener onLocationUpdateListener) {
        this.activity = activity;
        this.onLocationUpdateListener = onLocationUpdateListener;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        createLocationRequest();
        getDeviceLocation();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //Последние координаты в списке - самые новые
                    Location location = locationList.get(locationList.size() - 1);
                    mLastKnownLocation = location;
                    if (onLocationUpdateListener != null) {
                        onLocationUpdateListener.onLocationChange(location);
                        if(updateStartedInternally){
                            stopLocationUpdate();
                        }
                    }
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getDeviceLocation() {
        //Получаем самое точное местоположение(прошлое), в случаях, когда определить его не получается
        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(activity.getMainExecutor(), task -> {
                if (task.isSuccessful()) {
                    //Ставим позицию на карте  - положение устройства в данный момент
                    mLastKnownLocation = (Location) task.getResult();
                    if (mLastKnownLocation == null) {
                        updateStartedInternally = true;
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        onLocationUpdateListener.onLocationChange(mLastKnownLocation);
                    }
                } else {
                    onLocationUpdateListener.onError("Can't get Location");
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
            onLocationUpdateListener.onError(e.getMessage());

        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        updateStartedInternally = false;
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void stopLocationUpdate() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }




    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);//временной интервал получения координат
        mLocationRequest.setFastestInterval(5000);//если уже доступны координаты (от другого приложения)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
