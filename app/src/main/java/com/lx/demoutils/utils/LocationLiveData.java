package com.lx.demoutils.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * com.lx.demoutils.utils
 * DemoUtils
 * Created by lixiao2
 * 2018/9/25.
 */

public class LocationLiveData extends LiveData<Location> {
    LocationManager manager;
    private Context mContext;
    private LocationListener mListener;

    public LocationLiveData(Context context, LocationManager m) {
        mContext = context;
        manager = m;
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setValue(location);
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
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = manager.getBestProvider(criteria, true);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
    }

    @Override
    protected void onInactive() {
        manager.removeUpdates(mListener);
    }
}
